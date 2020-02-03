package zbh.study.dto;

import lombok.Data;
import zbh.study.domain.User;

@Data
public class ProductDetailDTO {
	private int buyStatus = 0;
	private int remainSeconds = 0;
	private ProductDTO productDTO ;
	private User user;
}
