package zbh.study.controller;

import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import zbh.study.domain.Product;
import zbh.study.domain.User;
import zbh.study.dto.ProductDTO;
import zbh.study.dto.ProductDetailDTO;
import zbh.study.redis.RedisKeyPrefix;
import zbh.study.result.CodeMsg;
import zbh.study.result.Result;
import zbh.study.service.ProductService;
import zbh.study.service.UserService;
import zbh.study.vo.ProductAddVO;
import zbh.study.vo.PublishVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


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
    @RequestMapping(value="/detail/all")
    public String manage( Model model, User user) {
        if (user == null) {
            return "login";
        }
        List<ProductDTO> productDTOList = productService.listProductsAll();
        model.addAttribute("productDTOList", productDTOList);
        model.addAttribute("user", user);
        return "manage";
    }
    @GetMapping("/manage-list")
    public String listForManage(User user,Model model){
        if (user == null) {
            return "login";
        }
        model.addAttribute("productList",productService.getRest());
        model.addAttribute("user", user);
        return "to_add";
    }

    @PostMapping("/add")
    @ResponseBody
    public Result<Long> add(User user, ProductAddVO vo,HttpServletRequest request) throws FileNotFoundException {

        MultipartFile file=vo.getProductImg();
        if (user == null || file==null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        String filename = System.currentTimeMillis()+file.getOriginalFilename();
        String path = ResourceUtils.getURL("classpath:").getPath()+"static/img";
        String realPath = path.replace('/', '\\').substring(1,path.length());
        File target=new File(realPath+"\\"+filename);

        try {
            file.transferTo(target);
        } catch (IOException e) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        String dbPath="/img/"+filename;
        Product p= new Product();
        BeanUtils.copyProperties(vo, p);
        p.setProductImg(dbPath);
        productService.add(p);
        return Result.success(p.getId());
    }
    @GetMapping("/to-publish")
    public String toPublish(Model model,User user,
                            @RequestParam(required = false,value = "update") Boolean update,
                            @RequestParam("productId") long pid){

        model.addAttribute("user", user);
        if (update != null) {
            //dto
            model.addAttribute("product", productService.getById(pid));
            model.addAttribute("isUpdate", true);
        }
        else {
            Product product = productService.getProductById(pid);
            model.addAttribute("product", product);
            model.addAttribute("isUpdate", false);
        }
        return "publish";
    }
    @PostMapping("/publish")
    @ResponseBody
    public Result<Boolean> publish(User user, PublishVO vo,@RequestParam("isUpdate") Boolean update){
        if (user == null) {
            Result.error(CodeMsg.SESSION_ERROR);
        }
        //页面缓存清除
        redisTemplate.delete(RedisKeyPrefix.PAGE_PREFIX+"productList");
        if (update == null|| !update) {
            return Result.success(productService.publish(vo)==1);
        }
        return Result.success(productService.updatePublish(vo)==1);
    }
}
