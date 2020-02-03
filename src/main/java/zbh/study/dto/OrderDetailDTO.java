package zbh.study.dto;

import lombok.Data;
import zbh.study.domain.OrderDetail;


@Data
public class OrderDetailDTO {
    private ProductDTO productDTO;
    private OrderDetail orderDetail;
}
