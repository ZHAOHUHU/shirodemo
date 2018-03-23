package shenzhen.teamway.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import shenzhen.teamway.bean.User;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }
    @PostMapping(value = "/login")
    public String login(String username, String password) {
        Map map = new HashMap();
        final Subject subject = SecurityUtils.getSubject();
        final UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject.login(token);
            return "ok";
        } catch (LockedAccountException e) {
            map.put("msg", "账户锁定");
            return "login";
        } catch (AuthenticationException e) {
            map.put("msg", "用户名或者密码不对啊大兄弟");
            return "/login";
        }
    }

    @RequestMapping("/resources/delete")
    public String userpage() {
        System.out.println("走这里了嘛");
        return "/success";
    }


}
