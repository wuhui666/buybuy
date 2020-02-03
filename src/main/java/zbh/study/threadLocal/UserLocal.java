package zbh.study.threadLocal;

import zbh.study.domain.User;

public class UserLocal {
    private static ThreadLocal<User> userThreadLocal=new ThreadLocal<>();

    public static User getUser() {
        return userThreadLocal.get();
    }
    public static void setUser(User u) {
        userThreadLocal.set(u);
    }
    // 防止内存泄漏
    public static void remove() {
        userThreadLocal.remove();
    }

}
