package zbh.study;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zbh.study.dao.OrderDetailDAO;
import zbh.study.domain.OrderDetail;
import zbh.study.domain.Product;
import zbh.study.domain.User;
import zbh.study.dto.ProductDTO;

import java.util.Date;

/**
 * Unit test for simple App.
 */
@SpringBootTest(classes = App.class)
@RunWith(SpringRunner.class)
public class AppTest 
{
    @Autowired
    OrderDetailDAO dao;
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        User user=new User();
        user.setId(13720201236L);
        ProductDTO product=new ProductDTO();
        product.setId(1002L);
        product.setProductName("iphone11");
        product.setBuyPrice(4199.9);
        // 插入订单详情
        OrderDetail order=new OrderDetail();
        order.setUserId(user.getId());
        order.setProductId(product.getId());
        order.setAddress("武汉市洪山区湖北工业大学");
        order.setProductCount(1);
        order.setCreateDate(new Date());
        order.setProductName(product.getProductName());
        order.setProductPrice(product.getBuyPrice());
        order.setStatus(0);
        System.out.println(order.toString());
        dao.insert(order);
    }
}
