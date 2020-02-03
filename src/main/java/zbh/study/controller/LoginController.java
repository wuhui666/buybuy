package zbh.study.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import zbh.study.result.Result;
import zbh.study.service.UserService;
import zbh.study.vo.LoginVO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author: wuhui
 * @time: 2019/9/6 22:12
 * @desc:
 */
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

    @RequestMapping("/do-login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVO loginVo) {
        //登录
        return Result.success(userService.login(response, loginVo));
    }

}
