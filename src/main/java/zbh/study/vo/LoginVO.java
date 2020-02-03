package zbh.study.vo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class LoginVO {
	
	@NotNull
	@Pattern(regexp = "^1[3456789]\\d{9}$",message = "手机格式不合法")
	private String mobile;
	
	@NotNull
	@Length(min=32)
	private String password;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "LoginVo [mobile=" + mobile + ", password=" + password + "]";
	}
}
