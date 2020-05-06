package zbh.study.dto;

import lombok.Data;

/**
 * @author: wuhui
 * @time: 2020/4/29 18:09
 * @desc:
 */
@Data
public class OrderListDTO {
    private Long Id;
    private String productName;
    private String productImg;
    private Integer productCount;
    private Double productPrice;
    private Integer status;
}
