package zbh.study.result;

public class CodeMsg {

	private int code;
	private String msg;

	//通用的错误码
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
	//登录模块 5002XX
	public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效,重新登录");
	public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "用户不存在");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");


	//注册模块 5003XX
	public static CodeMsg MOBILE_HAS_EXIST = new CodeMsg(500215, "用户已存在");


	//订单模块 5004XX
	public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "订单已经失效或不存在");

	//秒杀模块 5005XX
	public static CodeMsg BUY_FAIL = new CodeMsg(500500, "商品已经被抢完了");
	public static CodeMsg BUY_REPEAT = new CodeMsg(500501, "不能重复秒杀");
	public static CodeMsg BUY_DENIED = new CodeMsg(500502, "秒杀被拒绝,请勿通过非法途径秒杀");
	public static CodeMsg BUY_VERIFY_ERROR = new CodeMsg(500503, "验证码错误");
	public static CodeMsg BUY_REQUEST_LIMITED = new CodeMsg(500504, "过于频繁");


	private CodeMsg( ) {
	}

	private CodeMsg( int code,String msg ) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public CodeMsg fillArgs(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		return new CodeMsg(code, message);
	}

	@Override
	public String toString() {
		return "CodeMsg [code=" + code + ", msg=" + msg + "]";
	}
	
	
}
