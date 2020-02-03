package zbh.study.config;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import zbh.study.domain.User;
import zbh.study.threadLocal.UserLocal;


@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 本地线程取用
        User user = UserLocal.getUser();
        // 用完删除避免内存泄漏
        //UserLocal.remove();
        return user;
    }

    /* 如果controller方法参数有user,返回真，执行逻辑
    * */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType== User.class;
    }
}
