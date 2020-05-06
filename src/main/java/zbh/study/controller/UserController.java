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
            user.setPassword("");
            return Result.success(user);
        }
        return Result.error(CodeMsg.MOBILE_NOT_EXIST);
    }

}
