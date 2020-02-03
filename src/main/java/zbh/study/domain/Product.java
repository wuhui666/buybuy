package zbh.study.domain;

import lombok.Data;


@Data
public class Product {
    private Long id;
    private String productName;
    private String productTitle;
    private String productImg;
    private String productDetail;
    private Double productPrice;

}
