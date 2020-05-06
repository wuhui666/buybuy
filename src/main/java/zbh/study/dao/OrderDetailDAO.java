package zbh.study.dao;

import org.apache.ibatis.annotations.*;
import zbh.study.domain.OrderDetail;
import zbh.study.dto.OrderListDTO;

import java.util.Date;
import java.util.List;


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
    public void deleteOrdersForReset();

    @Delete("delete from order_detail where id = #{orderId}")
    public void deleteOrdersById(@Param("orderId")long orderId);

    @Update("update order_detail set status=#{status} where id = #{orderId}")
    public int updateStatus(@Param("orderId")long orderId,@Param("status")int status);
    @Update("update order_detail set pay_date=#{date} where id = #{orderId}")
    void updatePayDate(long orderId,@Param("date") Date date);

    @Select("select o.id,p.product_name,p.product_img,o.product_count,o.product_price,o.status" +
            " from order_detail o,product p where o.product_id=p.id and o.user_id=#{id}")
    @Results(
            value = {
                    @Result(column = "id",property = "Id"),
                    @Result(column = "product_name",property = "productName"),
                    @Result(column = "product_img",property = "productImg"),
                    @Result(column = "product_count",property = "productCount"),
                    @Result(column = "product_price",property = "productPrice"),
                    @Result(column = "status",property = "status"),
            }
    )
    List<OrderListDTO> getOrdersByUid(Long id);
}
