package zbh.study.dao;

import org.apache.ibatis.annotations.*;
import zbh.study.domain.OrderDetail;

/**
 * @author: wuhui
 * @time: 2019/9/9 8:58
 * @desc:
 */
@Mapper
public interface OrderDetailDAO {
    @Insert("insert into " +
            "order_detail(user_id,product_id,address,product_name,product_count,product_price,status,create_date) " +
            "values(#{ord.userId},#{ord.productId},#{ord.address},#{ord.productName},#{ord.productCount},#{ord.productPrice},#{ord.status},#{ord.createDate})")
    @SelectKey(keyColumn = "id",keyProperty = "ord.id",resultType = Long.class,before = false,statement = "select last_insert_id()")
    public long insert(@Param("ord") OrderDetail order);
    @Select("select * from order_detail where id = #{orderId}")
    public OrderDetail getOrderById(@Param("orderId")long orderId);

    @Delete("delete from order_detail")
    public void deleteOrders();
}
