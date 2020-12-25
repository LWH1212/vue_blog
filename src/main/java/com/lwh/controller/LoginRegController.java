package com.lwh.controller;

import com.lwh.bean.RespBean;
import com.lwh.bean.User;
import com.lwh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

@RestController
public class LoginRegController {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    UserService userService;

    @RequestMapping("/login_error")
    public RespBean loginError(){
        return new RespBean("error","登录失败");
    }

    @RequestMapping("/login_success")
    public RespBean loginSuccess(){
        return new RespBean("success","登录成功");
    }

    @RequestMapping("/login_page")
    public RespBean loginPage(){
        return new RespBean("error","尚未登录，请登录!");
    }

    @PostMapping("/reg")
    public RespBean reg(User user,HttpSession session){
        String userface = (String) session.getAttribute("userface");
        user.setUserface("/img/"+userface);
        int result = userService.reg(user);
        if (result == 0){
            //成功
            return new RespBean("success","注册成功!");
        }else if (result == 1){
            return new RespBean("error","用户名重复，注册失败！");
        }else {
            return new RespBean("error","注册失败");
        }
    }

    //上传图片
    @RequestMapping(value = "/uploaduserface",method = RequestMethod.POST)
    public RespBean uploadImg(MultipartFile userface, HttpSession session) {
        String filePath1 = "D:/lwh/vue_blog/src/main/resources/static/img/";
        String filename = UUID.randomUUID()+"_"+userface.getOriginalFilename();
        File imgFolder1 = new File(filePath1+ filename);
        session.setAttribute("userface",filename);
        if (!imgFolder1.getParentFile().exists()){
            imgFolder1.getParentFile().mkdirs();
        }
        try {
            userface.transferTo(imgFolder1);
            return new RespBean("success","上传成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RespBean("error","上传失败");
    }
}
