package zbh.study.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author: wuhui
 * @time: 2020/5/4 13:12
 * @desc:
 */
@Data
public class PublishVO {
    @NotNull(message = "产品参数为空")
    private Long productId;
    @NotNull(message = "产品库存为空")
    private Integer buyStock;
    @NotNull(message = "产品价格为空")
    private Double buyPrice;
    @NotNull(message = "开始日期为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startDate;
    @NotNull(message = "结束日期为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endDate;
}
