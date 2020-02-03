package zbh.study.domain;

import lombok.Data;


@Data
public class BuyOrder {
    private Long id;
    private Long userId;
    private Long  orderId;
    private Long productId;
}
