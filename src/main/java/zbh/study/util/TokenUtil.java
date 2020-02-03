package zbh.study.util;

import java.util.UUID;

/**
 * @author: wuhui
 * @time: 2019/9/7 21:00
 * @desc:
 */
public class TokenUtil {
    public static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
