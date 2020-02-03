package zbh.study.dao;

import org.apache.ibatis.annotations.*;
import zbh.study.domain.BuyOrder;


@Mapper
public interface BuyOrderDAO {
    @Select("select count(*) from buy_order where user_id=#{uid} and product_id=#{pid}")
    Integer getByUserAndProduct(@Param("uid") long uid, @Param("pid") long pid);

    @Insert("insert into buy_order(user_id,order_id,product_id) values(#{userId},#{orderId},#{productId})")
    @SelectKey(keyColumn = "id",keyProperty = "id",resultType = long.class,before = false,statement = "select last_insert_id()")
    // @Options(useGeneratedKeys=true, keyProperty="s.id", keyColumn="id")
    long insert(BuyOrder buyOrder);

    @Delete("delete from buy_order")
    public void deletebuyOrders();
}
