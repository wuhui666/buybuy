package zbh.study.util;

import org.apache.commons.codec.digest.DigestUtils;


public class MD5Util {
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "3j9i2b7d";

    // 第一次md，固定salt,前端将输入密码加密后生成表单数据，提交给后端
    public static String inputPassToFormPass(String inputPass) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);

        return md5(str);
    }
    // 第二次md,随机salt
    public static String formPassToDBPass(String formPass, String salt) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String inputPass, String saltDB) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }
}
