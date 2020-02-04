package zbh.study.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import zbh.study.anotations.RequestLimit;
import zbh.study.domain.OrderDetail;
import zbh.study.domain.User;
import zbh.study.dto.ProductDTO;
import zbh.study.mq.MqSender;
import zbh.study.mq.BuyMessage;
import zbh.study.redis.RedisKeyPrefix;
import zbh.study.result.CodeMsg;
import zbh.study.result.Result;
import zbh.study.service.ProductService;
import zbh.study.service.BuyOrderService;
import zbh.study.service.BuyService;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.List;


@Controller
public class BuyController implements InitializingBean {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductService productService;
    @Autowired
    BuyOrderService buyOrderService;
    @Autowired
    BuyService buyService;
    @Autowired
    MqSender sender;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<ProductDTO> products = productService.listProducts();
        if(products == null||products.isEmpty()) {
            return;
        }
        for(ProductDTO product : products) {
            redisTemplate.opsForValue().set(RedisKeyPrefix.PRODUCT_STOCK+product.getId(), product.getBuyStock().toString());
        }
    }

    @PostMapping("/{path}/buy")
    @ResponseBody
    public Result doBuy(
            User user, Model model,
            Long productId,
            @PathVariable("path") String path){
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 检查缓存中是否找得到path
        String s = redisTemplate.opsForValue().get(RedisKeyPrefix.BUY_PATH_STRING + user.getId() + "_" + productId);

        if (StringUtils.isBlank(s)){
            // TODO:  注释掉以便进行测试
            return Result.error(CodeMsg.BUY_DENIED);
        }
        //判断是否已经秒杀到了
        OrderDetail orderDetail = buyOrderService.getByUserAndProduct(user.getId(), productId);
        if(orderDetail != null) {
            // TODO:  注释掉以便进行测试
            return Result.error(CodeMsg.BUY_REPEAT);
        }
        //判断库存
        String stock = redisTemplate.opsForValue().get(RedisKeyPrefix.PRODUCT_STOCK+productId);
        if(Long.valueOf(stock) <= 0) {
            return Result.error(CodeMsg.BUY_FAIL);
        }
        model.addAttribute("user", user);

        //入队
        BuyMessage message = new BuyMessage();
        message.setUser(user);
        message.setProductId(productId);
        sender.sendMessage(message);
        return Result.success(0);//排队中

    }
    /**
     * 订单号：成功
     * -1：秒杀失败，库存不足
     * -2，秒杀失败，服务器异常
     * 0： 排队中
     * */
    @RequestMapping(value="/buy/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> buyResult(Model model,User user,
                                      @RequestParam("productId")long productId) {

        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  = buyService.getBuyResult(user.getId(), productId);
        return Result.success(result);
    }
    @RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<ProductDTO> products = productService.listProducts();
        for(ProductDTO productDTO : products) {
            productDTO.setBuyStock(10);
            redisTemplate.opsForValue().set(RedisKeyPrefix.PRODUCT_STOCK+productDTO.getId(), JSON.toJSONString(productDTO));

        }
        // 删除缓存
        redisTemplate.delete(redisTemplate.keys(RedisKeyPrefix.BUY_ORDER_DETAIL + "*"));
        redisTemplate.delete(redisTemplate.keys(RedisKeyPrefix.PRODUCT_STOCK + "*"));
        redisTemplate.delete(redisTemplate.keys(RedisKeyPrefix.PRODUCT_STOCK + "*"));


        // 重置
        buyService.reset(products);
        // 初始化缓存库存
        List<ProductDTO> list = productService.listProducts();
        if(list != null) {
            for(ProductDTO product : list) {
                redisTemplate.opsForValue().set(RedisKeyPrefix.PRODUCT_STOCK+product.getId(), product.getBuyStock().toString());
            }
        }
        return Result.success(true);
    }

    // 限流注解
    @RequestLimit(seconds = 5,maxCount = 5,loginRequired = true)
    @RequestMapping(value="/buy/path", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getBuyPath(HttpServletRequest request, User user,
                                         @RequestParam("productId")long productId,
                                         @RequestParam(value="verifyCode", defaultValue="0")int verifyCode
    ) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = buyService.checkVerifyCode(user, productId, verifyCode);
        if(!check) {
            return Result.error(CodeMsg.BUY_VERIFY_ERROR);
        }
        String path  = buyService.createBuyPath(user, productId);
        return Result.success(path);
    }

    @RequestMapping(value="/buy/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getBuyVerifyCode(HttpServletResponse response, User user,
                                              @RequestParam("productId")long productId) {

        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image  = buyService.createVerifyCode(user, productId);
            OutputStream out = response.getOutputStream();
            // 写图片
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
