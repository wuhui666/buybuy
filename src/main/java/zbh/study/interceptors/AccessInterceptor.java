package zbh.study.interceptors;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import zbh.study.anotations.RequestLimit;
import zbh.study.domain.User;
import zbh.study.exception.GlobalException;
import zbh.study.result.CodeMsg;
import zbh.study.result.Result;
import zbh.study.service.UserService;
import zbh.study.threadLocal.UserLocal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    UserService userService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 只对@requestLimit的controller做处理
        if (handler instanceof HandlerMethod){
            // 保存到本地线程，方便其他地方直接取用
            User user=getUser(request,response);
            UserLocal.setUser(user);
           HandlerMethod handlerMethod=(HandlerMethod)handler;
           RequestLimit accessLimit = handlerMethod.getMethodAnnotation(RequestLimit.class);
           // 未加注解，直接放行，
            // user是否已经登录的判断需要根据controller参数决定是否需要判断，
            // 交给UserArgumentResolver（在拦截器之后执行）
           if (accessLimit == null) {
               return true;
           }
           int seconds = accessLimit.seconds();
           int maxCount = accessLimit.maxCount();
           boolean loginRequired = accessLimit.loginRequired();


           String key=request.getRequestURI();
           if (loginRequired){
               if (user == null) {
                   createResponse(response, CodeMsg.SESSION_ERROR);
                   return false;
               }
               key=key+"_"+user.getId();
           }
           String count = redisTemplate.opsForValue().get(key);
           if (count == null){
               // seconds 秒过期
               redisTemplate.opsForValue().set(key,"1", seconds, TimeUnit.SECONDS);
           }
           // seconds 秒请求未达到maxCount次,
           else if(Integer.valueOf(count)<maxCount){
               redisTemplate.opsForValue().increment(key, 1);
           }
           // 到达限流
           else {
               createResponse(response, CodeMsg.BUY_REQUEST_LIMITED);
               return false;
           }
       }
        return true;
    }

    private void createResponse(HttpServletResponse response, CodeMsg codeMsg) {
        // 防止乱码需要下面两行
        //让浏览器用utf8来解析返回的数据
        response.setContentType("application/json;charsetEncoding=UTF-8");
        //告知servlet用UTF-8转码
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(JSON.toJSONString(Result.error(codeMsg)));
        } catch (IOException e) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies=request.getCookies();

        String tokenFromCookie="";
        String tokenFromURL="";
        String token="";
        tokenFromURL=request.getParameter(UserService.COOKIE_NAME_TOKEN);
        if (cookies!=null&&cookies.length>0){
            for(Cookie c:cookies){
                if (c.getName().equals(UserService.COOKIE_NAME_TOKEN)){
                    tokenFromCookie=c.getValue();
                }
            }
        }
        // token不存在
        /*if (StringUtils.isBlank(tokenFromCookie)&&StringUtils.isBlank(tokenFromURL)){
            throw new GlobalException(CodeMsg.SESSION_ERROR);
        }*/
        token=StringUtils.isBlank(tokenFromURL)?tokenFromCookie:tokenFromURL;
        User user=userService.findUserByToken(response,token);

        return user;
    }
}
