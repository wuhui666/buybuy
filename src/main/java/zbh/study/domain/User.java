package zbh.study.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author: wuhui
 * @time: 2019/9/6 23:09
 * @desc:
 */
@Data
public class User{
    private Long id;
    private String nickname;
    private String password;
    private String salt;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;

}
