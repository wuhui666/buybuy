package zbh.study.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zbh.study.dao.ProductDAO;
import zbh.study.domain.BuyProduct;
import zbh.study.domain.Product;
import zbh.study.dto.ProductDTO;

import java.util.List;

/**
 * @author: wuhui
 * @time: 2019/9/8 17:00
 * @desc:
 */
@Service
public class ProductService {
    @Autowired
    ProductDAO dao;

    public ProductDTO getById(long id){
        return dao.getById(id);
    }
    public List<ProductDTO> listProducts(){
        return dao.listProductDTO();
    }

    //减秒杀商品库存
    public boolean reduceStock(ProductDTO product) {
        BuyProduct p=new BuyProduct();
        p.setProductId(product.getId());
        p.setBuyStock(product.getBuyStock()-1);
        int count = dao.updateByProductIdSelective(p);
        return count>0;

    }

    public void resetStock(List<ProductDTO> products) {
        for(ProductDTO p : products ) {
            ProductDTO g = new ProductDTO();
            g.setId(p.getId());
            g.setBuyStock(p.getBuyStock());
            dao.resetStock(g);
        }
    }
}
