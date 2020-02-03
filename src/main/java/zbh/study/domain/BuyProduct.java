package zbh.study.domain;
import lombok.Data;

import	java.util.Date;

import java.util.Date;

/**
 * @author: wuhui
 * @time: 2019/9/8 16:29
 * @desc:
 */
@Data
public class BuyProduct {
    private Long id;
    private Long productId;
    private Integer buyStock;
    private Double buyPrice;
    private Date startDate;
    private Date endDate;
}
