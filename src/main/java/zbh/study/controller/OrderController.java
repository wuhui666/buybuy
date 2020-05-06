package zbh.study.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import zbh.study.domain.OrderDetail;
import zbh.study.domain.User;
import zbh.study.dto.OrderDetailDTO;
import zbh.study.dto.OrderListDTO;
import zbh.study.dto.ProductDTO;
import zbh.study.redis.RedisKeyPrefix;
import zbh.study.result.CodeMsg;
import zbh.study.result.Result;
import zbh.study.service.BuyOrderService;
import zbh.study.service.ProductService;
import zbh.study.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	UserService userService;
	
	@Autowired
	StringRedisTemplate redisTemplate;
	
	@Autowired
	BuyOrderService orderService;
	
	@Autowired
	ProductService productService;
	
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailDTO> info(HttpServletRequest request, User user,
									   @RequestParam("orderId") long orderId) {
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	OrderDetail detail = orderService.getOrderById(orderId);
    	if(detail == null) {
    		return Result.error(CodeMsg.ORDER_NOT_EXIST);
    	}
    	long productId = detail.getProductId();
    	ProductDTO productDTO = productService.getById(productId);
    	OrderDetailDTO detailDTO = new OrderDetailDTO();
		detailDTO.setOrderDetail(detail);
		detailDTO.setProductDTO(productDTO);
		detailDTO.setUser(user);
    	return Result.success(detailDTO);
    }
	@RequestMapping("/orders_list")
	public String orderList(User user,Model model){
    	if (user==null){
    		return "login";
		}
    	model.addAttribute("ordersList",orderService.getOrdersByUid(user.getId()));
    	model.addAttribute("user", user);
    	return "orders_list";
	}
}
