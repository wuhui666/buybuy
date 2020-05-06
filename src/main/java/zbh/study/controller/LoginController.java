package zbh.study.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import zbh.study.domain.User;
import zbh.study.result.Result;
import zbh.study.service.UserService;
import zbh.study.vo.LoginVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;


@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    UserService userService;

    @RequestMapping("/to-login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do-logout")
    @ResponseBody
    public Result<Boolean> logout(HttpServletRequest request,HttpServletResponse response,String userId) {
        long id=Long.parseLong(userId);
        //登出
        return Result.success(userService.logout(request,response, id));
    }
    @RequestMapping("/do-login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVO loginVo) {
        //登录
        return Result.success(userService.login(response, loginVo));
    }
    @RequestMapping("/register")
    @ResponseBody
    public Result<Boolean> register(@RequestParam(required = false,name = "update") String flag,
                                    String id, String nickname, String password, String address)
    {
        User user=new User();
        user.setId(Long.valueOf(id));
        user.setNickname(nickname);
        user.setPassword(password);
        user.setAddress(address);
        if (flag==null){
            return Result.success(userService.register(user));
        }
        else {
            return Result.success(userService.update(user)==1);
        }


    }
}
