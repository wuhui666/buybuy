package zbh.study.domain;

import lombok.Data;

import java.util.Date;


@Data
public class OrderDetail {
    private Long id;
    private Long userId;
    private Long productId;
    private String  address;
    private String productName;
    private Integer productCount;
    private Double productPrice;
    private Integer status;
    private Date createDate;
    private Date payDate;
}
