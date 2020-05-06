package zbh.study.dto;

import lombok.Data;
import zbh.study.domain.OrderDetail;
import zbh.study.domain.User;


@Data
public class OrderDetailDTO {
    private ProductDTO productDTO;
    private OrderDetail orderDetail;
    private  User user;
}
