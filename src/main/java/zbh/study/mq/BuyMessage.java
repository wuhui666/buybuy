package zbh.study.mq;

import lombok.Data;
import zbh.study.domain.User;


@Data
public class BuyMessage {
    private User user;
    private long productId;
}
