package zbh.study.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import zbh.study.domain.OrderDetail;
import zbh.study.domain.User;
import zbh.study.dto.OrderDetailDTO;
import zbh.study.dto.ProductDTO;
import zbh.study.redis.RedisKeyPrefix;
import zbh.study.service.BuyOrderService;
import zbh.study.service.BuyService;
import zbh.study.service.ProductService;


@Service
@Slf4j
public class MqReceiver {

    @Autowired
    ProductService productService;

    @Autowired
    BuyOrderService buyOrderService;

    @Autowired
    BuyOrderService orderService;

    @Autowired
    MqSender delaySender;

    @Autowired
    BuyService buyService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @RabbitListener(queues=MqConfig.BUYBUY_NORMAL_QUEUE)
    public void receive(String message){
            //log.info("receive message:"+message);
            BuyMessage mm  = JSON.parseObject(message).toJavaObject(BuyMessage.class);
            User user = mm.getUser();
            long productId = mm.getProductId();
            //数据库库存查询
            ProductDTO dto = productService.getById(productId);
            int stock = dto.getBuyStock();
            if(stock <= 0) {
                return;
            }
            //判断是否已经秒杀到了
            OrderDetail orderDetail = buyOrderService.getByUserAndProduct(user.getId(), productId);
            // TODO:  注释掉允许重复购买
           /* if(orderDetail != null) {
                return;
            }*/
            //减库存 下订单 写入秒杀订单
            OrderDetail detail=buyService.buy(user, dto);
            //订单限制时间支付
            delaySender.sendOrderToDelay(String.valueOf(detail.getId()));

    }

    @RabbitListener(queues=MqConfig.BUYBUY_DEAD_QUEUE)
    public void receiveDead(String message){
        long orderId = Long.valueOf(message);
        OrderDetail detail = buyOrderService.getOrderById(orderId);
        if(detail==null){
            return;
        }
        //已支付
        if (detail.getStatus()!=0){
            return;
        }
        //未支付，库存回扣
        Long productId = detail.getProductId();
        productService.increaseStockById(productId,1);
        // 数据库超时订单清除
        buyOrderService.deleteOrderById(orderId);
        // Redis清除
        redisTemplate.delete(RedisKeyPrefix.BUY_ORDER_DETAIL+detail.getUserId()+"_"+detail.getProductId());
    }


}
