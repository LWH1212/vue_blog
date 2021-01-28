package com.lwh.controller;

import com.lwh.bean.RespBean;
import com.lwh.bean.User;
import com.lwh.service.UserService;
import com.lwh.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/currentUserName")
    public String currentUserName(){
        return Util.getCurrentUser().getNickname();
    }

    @RequestMapping("/currentUserId")
    public Long currentUserId(){
        return Util.getCurrentUser().getId();
    }

    @RequestMapping("/currentUserEmail")
    public String currentUserEmail(){
        return Util.getCurrentUser().getEmail();
    }

    @RequestMapping("/isAdmin")
    public Boolean isAdmin(){
        List<GrantedAuthority> authorities = Util.getCurrentUser().getAuthorities();
        for (GrantedAuthority authority : authorities){
            if (authority.getAuthority().contains("超级管理员")){
                return true;
            }
        }
        return false;
    }

    @PutMapping("/updateUserEmail")
    public RespBean updateUserEmail(String email){
        if (userService.updateUserEmail(email) == 1){
            userService.updateUserOpenComment(Util.getCurrentUser().getId());
            return new RespBean("success","开启成功");
        }
        return new RespBean("error","开启失败");
    }

    @RequestMapping(value = "/close",method = RequestMethod.PUT)
    public RespBean updateUserClose(){
        if (userService.updateUserCloseComment(Util.getCurrentUser().getId()) == 1){
            return new RespBean("success","关闭成功");
        }else {
            return new RespBean("error","关闭失败");
        }
    }

    @GetMapping("/currentUserInfo")
    public User currentUserInfo(){
        return Util.getCurrentUser();
    }

}
