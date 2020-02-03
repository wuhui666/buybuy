package zbh.study.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import zbh.study.domain.User;
import zbh.study.redis.RedisKeyPrefix;
import zbh.study.result.CodeMsg;
import zbh.study.result.Result;
import zbh.study.service.UserService;


@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    StringRedisTemplate redisTemplate;

    @RequestMapping("/user/{id}")
    @ResponseBody
    public Result getUser(@PathVariable long id) {
        User user = userService.findUserById(id);
        if (user!=null){
            return Result.success(user);
        }
        return Result.error(CodeMsg.MOBILE_NOT_EXIST);
    }
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result redisSet() {
       User user=new User();
       user.setId(13720201236L);
       user.setNickname("hhh");
       redisTemplate.opsForValue().set(RedisKeyPrefix.USER_ID_PREFIX+user.getId(), JSON.toJSONString(user));

       return  Result.success("成功");
    }
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result redisGet() {
        String s = redisTemplate.opsForValue().get("13720201236");
        if (s==null){
            return  Result.error(CodeMsg.MOBILE_NOT_EXIST);
        }
        return  Result.success(s);
    }
    @RequestMapping("/user/test")
    @ResponseBody
    public Result test(User user) {
        System.out.println(user);
        return Result.success(user);
    }
}
