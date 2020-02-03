package zbh.study.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zbh.study.domain.OrderDetail;
import zbh.study.domain.User;
import zbh.study.dto.ProductDTO;
import zbh.study.service.ProductService;
import zbh.study.service.BuyOrderService;
import zbh.study.service.BuyService;


@Service
@Slf4j
public class MqReceiver {

    @Autowired
    ProductService productService;

    @Autowired
    BuyOrderService buyOrderService;

    @Autowired
    BuyService buyService;
    @RabbitListener(queues=MqConfig.SECKILL_QUEUE)
    public void receive(String message){
            log.info("receive message:"+message);
            BuyMessage mm  = JSON.parseObject(message).toJavaObject(BuyMessage.class);
            User user = mm.getUser();
            long productId = mm.getProductId();
            ProductDTO dto = productService.getById(productId);
            int stock = dto.getBuyStock();
            if(stock <= 0) {
                return;
            }
            //判断是否已经秒杀到了
            OrderDetail orderDetail = buyOrderService.getByUserAndProduct(user.getId(), productId);
            if(orderDetail != null) {
                return;
            }
            //减库存 下订单 写入秒杀订单
            buyService.buy(user, dto);

    }



}
