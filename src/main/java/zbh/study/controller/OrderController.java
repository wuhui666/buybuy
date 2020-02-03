package zbh.study.controller;

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
import zbh.study.dto.ProductDTO;
import zbh.study.result.CodeMsg;
import zbh.study.result.Result;
import zbh.study.service.OrderService;
import zbh.study.service.ProductService;
import zbh.study.service.UserService;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	UserService userService;
	
	@Autowired
	StringRedisTemplate redisTemplate;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	ProductService productService;
	
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailDTO> info(Model model, User user,
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
    	return Result.success(detailDTO);
    }
    
}
