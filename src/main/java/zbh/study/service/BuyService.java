package zbh.study.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zbh.study.domain.*;
import zbh.study.dto.ProductDTO;
import zbh.study.redis.RedisKeyPrefix;
import zbh.study.util.MD5Util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class BuyService {
    @Autowired
    ProductService productService;
    @Autowired
    BuyOrderService buyOrderService;
    @Autowired
    StringRedisTemplate template;
    @Autowired
    OrderService orderService;

    @Transactional(rollbackFor = Exception.class)
    public OrderDetail buy(User user, ProductDTO product){
        try{
            // 减少缓存库存
            template.opsForValue().decrement(RedisKeyPrefix.PRODUCT_STOCK+product.getId());

            // 模拟出错，回滚
            //int i=3/0;

            // 减数据库库存
            boolean ok = productService.reduceStock(product);
            if (ok){
                // 插入订单
                OrderDetail detail = buyOrderService.createOrder(user, product);
                return detail;
            }

            return null;
        }catch (Exception e){
            log.error("插入订单异常，事务回滚，秒杀失败");
            // redis回滚恢复缓存库存
            template.opsForValue().increment(RedisKeyPrefix.PRODUCT_STOCK+product.getId());
            // 插入失败记录到缓存,由于数据库未插入，id为null，表示失败
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setUserId(user.getId());
            orderDetail.setProductId(product.getId());
            template.opsForValue().set(RedisKeyPrefix.BUY_ORDER_DETAIL+user.getId()+"_"+product.getId(), JSON.toJSONString(orderDetail));

            return null;
        }

    }

    public long getBuyResult(Long uid, long pid) {
        String result = template.opsForValue().get(RedisKeyPrefix.BUY_ORDER_DETAIL+ uid + "_" + pid);
        // 缓存还没有,消息队列排队中
        if(result == null ) {
            return 0;
        }else {
            OrderDetail detail=JSON.parseObject(result).toJavaObject(OrderDetail.class);
            // 事务异常回滚导致的秒杀失败
            if (null == detail.getId()){
                return  -2;
            }
            String isOver = template.opsForValue().get(RedisKeyPrefix.PRODUCT_STOCK+pid);
            // 库存没了导致的秒杀失败
            if(StringUtils.isBlank(isOver)|| Long.valueOf(isOver)<=0) {
                return -1;
            }
            else {
                //秒杀成功,返回订单号
                return detail.getId();
            }
        }
    }

    public void reset(List<ProductDTO> products) {
        productService.resetStock(products);
        orderService.deleteOrders();
    }


    /** 检查接口路径的拼接部分
     * @param user
     * @param productId
     * @param path
     * @return
     */
    public boolean checkPath(User user, long productId, String path) {
        if(user == null || path == null) {
            return false;
        }
        String pathOld = template.opsForValue().get(RedisKeyPrefix.BUY_PATH_STRING+user.getId() + "_"+ productId);
        return path.equals(pathOld);
    }



    public String createBuyPath(User user, long productId) {
        if(user == null || productId <=0) {
            return null;
        }
        String str = MD5Util.md5(UUID.randomUUID().toString().replace("-", "") +"1salt233");
        // 60s后需要重新获取来拼接秒杀接口
        template.opsForValue().set(RedisKeyPrefix.BUY_PATH_STRING+user.getId() + "_"+ productId, str,RedisKeyPrefix.EXPIRE_TIME_MINUTE, TimeUnit.SECONDS);
        return str;
    }

    public BufferedImage createVerifyCode(User user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 90;
        int height = 33;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // 背景色
        g.setColor(new Color(0xE1FFFF));
        g.fillRect(0, 0, width, height);
        // 边框
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        
        Random rdm = new Random();
        // 干扰
        for (int i = 0; i < 90; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // 生成图片的表达式
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int v = getExpressionValue(verifyCode);
        template.opsForValue().set(RedisKeyPrefix.BUY_VERIFYCODE+user.getId()+"_"+goodsId, String.valueOf(v),RedisKeyPrefix.EXPIRE_TIME_MINUTE,TimeUnit.SECONDS);
        //输出图片
        return image;
    }

    /** js 引擎调用eval,计算字符串的运算结果
     * @param exp
     * @return
     */
    private static int getExpressionValue(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    public boolean checkVerifyCode(User user, long productId, int verifyCode) {
        if(user == null || productId <=0) {
            return false;
        }
        String codeOld = template.opsForValue().get(RedisKeyPrefix.BUY_VERIFYCODE+user.getId()+"_"+productId);
        if(codeOld == null || Integer.valueOf(codeOld) - verifyCode != 0 ) {
            return false;
        }
        template.delete(RedisKeyPrefix.BUY_VERIFYCODE+user.getId()+"_"+productId);
        return true;
    }
}
