package zbh.study.dao;
import	java.nio.channels.Selector;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.*;
import zbh.study.domain.BuyProduct;
import zbh.study.domain.Product;
import zbh.study.dto.ProductDTO;
import zbh.study.vo.PublishVO;


@Mapper
public interface ProductDAO {
    @Select("select p.*,sp.buy_price,sp.buy_stock,sp.start_date,sp.end_date " +
            "from product p " +
            "right join " +
            "buy_product sp on p.id=sp.product_id " +
            "where sp.end_date>#{date} order by sp.start_date")
    List<ProductDTO> listProductDTO(@Param("date") Date date);

    @Select("select p.*,sp.buy_price,sp.buy_stock,sp.start_date,sp.end_date from product p left join buy_product sp on p.id=sp.product_id where p.id=#{id}")
    ProductDTO getById(@Param("id") long id);


    /** 减秒杀商品的库存，注意防止库存为负
     * @param p
     */
    @Update("update buy_product set buy_stock=buy_stock-1 where product_id=#{productId} and buy_stock>0")
    int updateByProductIdSelective(BuyProduct p);

    @Update("update buy_product set buy_stock = #{buyStock} where product_id = #{id}")
    public int resetStock(ProductDTO g);

    @Update("update buy_product set buy_stock =buy_stock+#{num} where product_id = #{id}")
    void increaseStock(long id,int num);


    @Select("select * from product p where p.id not in \n" +
            "( SELECT bp.product_id from buy_product bp)")
    List<Product> getRest();

    @Insert("insert into product(product_name,product_price,product_detail,product_img,product_title)" +
            " values(#{productName},#{productPrice},#{productDetail},#{productImg},#{productTitle})")
    @SelectKey(keyColumn = "id",keyProperty = "id",resultType = long.class,before = false,statement = "select last_insert_id()")
    long insert(Product p);

    @Select("select * from product where id=#{pid}")
    Product getProductById(long pid);

    @Insert("insert into buy_product(product_id,buy_stock,buy_price,start_date,end_date) values(#{productId},#{buyStock},#{buyPrice},#{startDate},#{endDate})")
    int insertBuyProduct(PublishVO vo);

    @Update("update buy_product set buy_stock=#{buyStock}," +
            "buy_price=#{buyPrice},start_date=#{startDate}," +
            "end_date=#{endDate} where product_id=#{productId}")
    int updatePublish(PublishVO vo);

    @Select("select p.*,sp.buy_price,sp.buy_stock,sp.start_date,sp.end_date " +
            "from product p " +
            "right join " +
            "buy_product sp on p.id=sp.product_id " +
            "order by sp.start_date desc,sp.end_date")
    List<ProductDTO> listProductDTOAll();
}
