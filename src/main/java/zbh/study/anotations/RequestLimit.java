package zbh.study.anotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: wuhui
 * @time: 2019/9/16 10:06
 * @desc:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {
    int seconds();
    int maxCount();
    boolean loginRequired() default true;
}
