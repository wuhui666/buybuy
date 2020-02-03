package zbh.study.mq;

import lombok.Data;
import zbh.study.domain.User;

/**
 * @author: wuhui
 * @time: 2019/9/10 15:30
 * @desc:
 */
@Data
public class BuyMessage {
    private User user;
    private long productId;
}
