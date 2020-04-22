package zbh.study.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import zbh.study.dao.UserDAO;
import zbh.study.domain.User;
import zbh.study.exception.GlobalException;
import zbh.study.redis.RedisKeyPrefix;
import zbh.study.result.CodeMsg;
import zbh.study.result.Result;
import zbh.study.util.MD5Util;
import zbh.study.util.TokenUtil;
import zbh.study.vo.LoginVO;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;


@Service
public class UserService {
    public static final String COOKIE_NAME_TOKEN="_token";
    @Autowired
    UserDAO dao;
    @Autowired
    StringRedisTemplate template;

    public int addUser(User user) {
        return dao.addUser(user);
    }

    @Cacheable(value = "User",key = "#p0")
    public User findUserById(Long id){
        return dao.findUserById(id);
    }
    public User findUserByToken(HttpServletResponse response,String token){
        String userStr = template.opsForValue().get(RedisKeyPrefix.USER_TOKEN_PREFIX+token);
        User user = JSON.parseObject(userStr, User.class);
        // 每访问网页一次就刷新cookie和redis的缓存生存时间
        if (user!=null){
            addCookie(response, token, user);
        }
        return user;
    }

    public boolean login(HttpServletResponse response, LoginVO loginVo) {
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        User user = dao.findUserById(Long.parseLong(mobile));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        // 用户盐
        String saltDB = user.getSalt();
        // 二次加密后对比
        String resultPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!resultPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = TokenUtil.generateToken();
        addCookie(response, token, user);
        return true;
    }
    private void addCookie(HttpServletResponse response, String token, User user) {

        String userStr=JSON.toJSONString(user);
        template.opsForValue().set(RedisKeyPrefix.USER_TOKEN_PREFIX+token, userStr,RedisKeyPrefix.EXPIRE_TIME_DAY, TimeUnit.SECONDS);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(RedisKeyPrefix.EXPIRE_TIME_DAY.intValue());
        // 根路径，所以应用共享
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
