package zbh.study.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zbh.study.dao.BuyOrderDAO;
import zbh.study.dao.OrderDetailDAO;
import zbh.study.domain.BuyOrder;
import zbh.study.domain.OrderDetail;
import zbh.study.domain.User;
import zbh.study.dto.ProductDTO;
import zbh.study.redis.RedisKeyPrefix;

import java.util.Date;

@Service
public class OrderService {
	
	@Autowired
	OrderDetailDAO detailDAO;
	@Autowired
	BuyOrderDAO buyOrderDAO;
	
	@Autowired
	StringRedisTemplate template;
	
	public BuyOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {

		String s = template.opsForValue().get(RedisKeyPrefix.BUY_ORDER_DETAIL + userId + "_" + goodsId);

		return (BuyOrder) JSON.parse(s);
	}
	
	public OrderDetail getOrderById(long orderId) {
		return detailDAO.getOrderById(orderId);
	}
	

	@Transactional
	public OrderDetail createOrder(User user, ProductDTO productDTO) {
		OrderDetail orderInfo = new OrderDetail();
		orderInfo.setCreateDate(new Date());
		orderInfo.setAddress("武汉XXXX");
		orderInfo.setProductCount(1);
		orderInfo.setProductId(productDTO.getId());
		orderInfo.setProductName(productDTO.getProductName());
		orderInfo.setProductPrice(productDTO.getBuyPrice());
		orderInfo.setStatus(0);
		orderInfo.setUserId(user.getId());
		detailDAO.insert(orderInfo);
		BuyOrder seckillOrder = new BuyOrder();
		seckillOrder.setProductId(productDTO.getId());
		seckillOrder.setOrderId(orderInfo.getId());
		seckillOrder.setUserId(user.getId());
		buyOrderDAO.insert(seckillOrder);

		template.opsForValue().set(RedisKeyPrefix.BUY_ORDER_DETAIL+user.getId()+"_"+productDTO.getId(), JSON.toJSONString(seckillOrder));

		return orderInfo;
	}

	public void deleteOrders() {
		detailDAO.deleteOrders();
		buyOrderDAO.deletebuyOrders();
	}

}
