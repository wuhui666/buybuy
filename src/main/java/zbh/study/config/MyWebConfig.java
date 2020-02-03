package zbh.study.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import zbh.study.interceptors.AccessInterceptor;

import java.util.List;

/**
 * @author: wuhui
 * @time: 2019/9/7 23:00
 * @desc:
 */
@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    @Autowired
    UserArgumentResolver userArgumentResolver;
    @Autowired
    AccessInterceptor accessInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(userArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(accessInterceptor);
    }
}
