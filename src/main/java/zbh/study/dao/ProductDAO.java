package zbh.study.dao;
import	java.nio.channels.Selector;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import zbh.study.domain.BuyProduct;
import zbh.study.domain.Product;
import zbh.study.dto.ProductDTO;

/**
 * @author: wuhui
 * @time: 2019/9/8 16:47
 * @desc:
 */
@Mapper
public interface ProductDAO {
    @Select("select p.*,sp.buy_price,sp.buy_stock,sp.start_date,sp.end_date from product p left join buy_product sp on p.id=sp.product_id")
    List<ProductDTO> listProductDTO();

    @Select("select p.*,sp.buy_price,sp.buy_stock,sp.start_date,sp.end_date from product p left join buy_product sp on p.id=sp.product_id where p.id=#{id}")
    ProductDTO getById(@Param("id") long id);


    /** 减秒杀商品的库存，注意防止库存为负
     * @param p
     */
    @Update("update buy_product set buy_stock=buy_stock-1 where product_id=#{productId} and buy_stock>0")
    int updateByProductIdSelective(BuyProduct p);

    @Update("update buy_product set buy_stock = #{buyStock} where product_id = #{id}")
    public int resetStock(ProductDTO g);
}
