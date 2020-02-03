package zbh.study.domain;

import lombok.Data;

/**
 * @author: wuhui
 * @time: 2019/9/8 16:28
 * @desc:
 */
@Data
public class Product {
    private Long id;
    private String productName;
    private String productTitle;
    private String productImg;
    private String productDetail;
    private Double productPrice;

}
