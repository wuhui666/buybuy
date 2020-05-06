package zbh.study.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author: wuhui
 * @time: 2020/5/3 20:38
 * @desc:
 */
@Data
public class ProductAddVO {
    @NotNull
    private MultipartFile productImg;//名字与前端保持一致
    @NotNull
    private String productName;
    @NotNull
    private String productDetail;
    @NotNull
    private String productTitle;
    @NotNull
    private Double productPrice;

}
