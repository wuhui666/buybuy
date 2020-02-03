package zbh.study.domain;

import lombok.Data;

/**
 * @author: wuhui
 * @time: 2019/9/8 16:32
 * @desc:
 */
@Data
public class BuyOrder {
    private Long id;
    private Long userId;
    private Long  orderId;
    private Long productId;
}
