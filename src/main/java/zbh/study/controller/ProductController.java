package zbh.study.controller;

import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import zbh.study.domain.User;
import zbh.study.dto.ProductDTO;
import zbh.study.dto.ProductDetailDTO;
import zbh.study.redis.RedisKeyPrefix;
import zbh.study.result.CodeMsg;
import zbh.study.result.Result;
import zbh.study.service.ProductService;
import zbh.study.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: wuhui
 * @time: 2019/9/7 21:33
 * @desc:
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;


    @GetMapping(value = "/to-list",produces = "text/html")
    @ResponseBody
    public String to_list(HttpServletRequest request,HttpServletResponse response,Model model, User user){

        // 尝试从缓存读取页面
        String html = redisTemplate.opsForValue().get(RedisKeyPrefix.PAGE_PREFIX + "productList");
        // 找到缓存直接返回HTML页面
        if (!StringUtils.isBlank(html)){
            return html;
        }
        // 否则手动渲染返回，需要查询数据库
        List<ProductDTO> products = productService.listProducts();
        model.addAttribute("user", user);
        model.addAttribute("productList",products);
        WebContext ctx = new WebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap());
        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("products_list", ctx);
        // 缓存该页面60s
        if (!StringUtils.isBlank(html)){
            redisTemplate.opsForValue().set(RedisKeyPrefix.PAGE_PREFIX+"productList", html,RedisKeyPrefix.EXPIRE_TIME_MINUTE,TimeUnit.SECONDS);
        }
        return html;
    }

    // 供非静态化模板页面
    @GetMapping("/detail2/{id}")
    @ResponseBody
    public String list(HttpServletRequest request,HttpServletResponse response,
                       Model model, User user, @PathVariable long id){
        // 尝试从缓存读取页面
        String html = redisTemplate.opsForValue().get(RedisKeyPrefix.PAGE_PREFIX + "productDetail");
        // 找到缓存直接返回HTML页面
        if (!StringUtils.isBlank(html)){
            return html;
        }
        // 否则手动渲染返回，需要查询数据库
        ProductDTO productDTO = productService.getById(id);
        model.addAttribute("product", productDTO);
        long startAt = productDTO.getStartDate().getTime();
        long endAt = productDTO.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int seckillStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            seckillStatus = -1;
            remainSeconds = -1;
        }else {//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("user", user);
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        WebContext ctx = new WebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap());
        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("product_detail", ctx);
        // 缓存该页面60s
        if (!StringUtils.isBlank(html)){
            redisTemplate.opsForValue().set(RedisKeyPrefix.PAGE_PREFIX+"productDetail", html,RedisKeyPrefix.EXPIRE_TIME_MINUTE, TimeUnit.SECONDS);
        }
        return html;
    }

    // 供静态化html页面
    @RequestMapping(value="/detail/{productId}")
    @ResponseBody
    public Result<ProductDetailDTO> detail(HttpServletRequest request, HttpServletResponse response, Model model, User user,
                                           @PathVariable("productId")long productId) {


        ProductDTO productDTO = productService.getById(productId);
        long startAt = productDTO.getStartDate().getTime();
        long endAt = productDTO.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int buyStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            buyStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);//大于等于0
        }else  if(now > endAt){//秒杀已经结束
            buyStatus = 2;
            remainSeconds=0;
        }else {//秒杀进行中，remainSeconds此时代表还有多久秒杀结束
            buyStatus = 1;
            remainSeconds = (int)((now - endAt )/1000);//小于等于0
        }
        ProductDetailDTO detailDTO = new ProductDetailDTO();
        detailDTO.setProductDTO(productDTO);
        detailDTO.setUser(user);
        detailDTO.setRemainSeconds(remainSeconds);
        detailDTO.setBuyStatus(buyStatus);
        return Result.success(detailDTO);
    }
}
