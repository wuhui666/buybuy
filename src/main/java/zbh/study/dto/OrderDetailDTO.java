package zbh.study.dto;

import lombok.Data;
import zbh.study.domain.OrderDetail;

/**
 * @author: wuhui
 * @time: 2019/9/11 0:51
 * @desc:
 */
@Data
public class OrderDetailDTO {
    private ProductDTO productDTO;
    private OrderDetail orderDetail;
}
