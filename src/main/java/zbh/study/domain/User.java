package zbh.study.domain;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;


@Data
public class User{
    @NotNull
    @Pattern(regexp = "^1[3456789]\\d{9}$",message = "手机格式不合法")
    private Long id;
    @NotNull
    private String nickname;
    @NotNull
    private String password;
    private String salt;
    private Date registerDate;
    @NotNull
    private String address;

}
