package zbh.study.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zbh.study.dao.BuyOrderDAO;
import zbh.study.dao.OrderDetailDAO;
import zbh.study.domain.BuyOrder;
import zbh.study.domain.OrderDetail;
import zbh.study.domain.User;
import zbh.study.dto.OrderDetailDTO;
import zbh.study.dto.OrderListDTO;
import zbh.study.dto.ProductDTO;
import zbh.study.redis.RedisKeyPrefix;

import java.util.Date;
import java.util.List;

@Service
public class BuyOrderService {
    @Autowired
    BuyOrderDAO buyOrderDAO;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    OrderDetailDAO orderDetailDAO;
    public OrderDetail getByUserAndProduct(long uid, long pid){
        //return seckillOrderDAO.getByUserAndProduct(uid, pid);
        String json = redisTemplate.opsForValue().get(RedisKeyPrefix.BUY_ORDER_DETAIL + uid + "_" + pid);
        if (StringUtils.isBlank(json)){
            return null;
        }
        return JSON.parseObject(json).toJavaObject(OrderDetail.class);
    }


    @Transactional(rollbackFor = Exception.class)
    public OrderDetail createOrder(User user, ProductDTO product) {

        // 插入订单详情
        OrderDetail order=new OrderDetail();
        order.setUserId(user.getId());
        order.setProductId(product.getId());
        order.setAddress(user.getAddress());
        order.setProductCount(1);
        order.setCreateDate(new Date());
        order.setProductName(product.getProductName());
        order.setProductPrice(product.getBuyPrice());
        order.setStatus(0);
        orderDetailDAO.insert(order);
        // 对应的秒杀表也插入记录
        BuyOrder buyOrder=new BuyOrder();
        buyOrder.setUserId(user.getId());
        buyOrder.setOrderId(order.getId());
        buyOrder.setProductId(product.getId());
        buyOrderDAO.insert(buyOrder);


        // 订单详情信息缓存到redis
        redisTemplate.opsForValue().set(RedisKeyPrefix.BUY_ORDER_DETAIL+user.getId()+"_"+product.getId(), JSON.toJSONString(order));

        return order;
    }

    public OrderDetail getOrderById(long orderId) {
        return orderDetailDAO.getOrderById(orderId);
    }
    public void deleteOrdersForReset() {
        orderDetailDAO.deleteOrdersForReset();
        buyOrderDAO.deletebuyOrdersForReset();
    }
    public int updateStatus(long orderId,int status){
        if (status==1){
            orderDetailDAO.updatePayDate(orderId,new Date());
        }
        return orderDetailDAO.updateStatus(orderId, status);
    }
    public void deleteOrderById(long id) {
        orderDetailDAO.deleteOrdersById(id);
        buyOrderDAO.deletebuyOrdersById(id);
    }

    public List<OrderListDTO> getOrdersByUid(Long id) {
       return orderDetailDAO.getOrdersByUid(id);
    }
}
