package zbh.study.dto;

import lombok.Data;
import zbh.study.domain.Product;

import java.util.Date;

/**
 * @author: wuhui
 * @time: 2019/9/8 16:49
 * @desc:
 */
@Data
public class ProductDTO extends Product {
    private Integer buyStock;
    private Double buyPrice;
    private Date startDate;
    private Date endDate;
}
