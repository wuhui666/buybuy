package zbh.study.redis;


public class RedisKeyPrefix {
    // 统一单位 秒
    public static final Long EXPIRE_TIME_MINUTE=60L;
    public static final Long EXPIRE_TIME_HOUR=60*EXPIRE_TIME_MINUTE;
    public static final Long EXPIRE_TIME_DAY=24*EXPIRE_TIME_HOUR;


    public static final String USER_TOKEN_PREFIX="BuyUser:token:";
    public static final String USER_LIMIT="User:limit:";
    public static final String PAGE_PREFIX="Pages:";
    public static final String PRODUCT_STOCK="ProductStock:";
    public static final String BUY_ORDER_DETAIL="BuyOrderDetail:";
    public static final String BUY_PATH_STRING="BuyPath:";
    public static final String BUY_VERIFYCODE="BuyVerifyCode:";
    public static final String BUY_RESULT="BuyResult:";
}
