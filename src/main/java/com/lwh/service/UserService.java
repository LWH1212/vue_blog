package com.lwh.service;

import com.lwh.bean.Role;
import com.lwh.bean.User;
import com.lwh.mapper.RolesMapper;
import com.lwh.mapper.UserMapper;
import com.lwh.util.UploadImageUtil;
import com.lwh.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RolesMapper rolesMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userMapper.loadUserByUsername(s);
        if (user == null){
            //避免返回null，这里返回一个不含有任何值的user对象，在后期的密码比对过程中一样会验证失败
            return new User();
        }
        //查询用户的角色信息，并返回存入user中
        List<Role> roles = rolesMapper.getRolesByUid(user.getId());
        user.setRoles(roles);
        return user;
    }

    /**
     * @param user
     * @return 0表示成功
     * 1表示用户名重复
     * 2表示失败
    */
    public int reg(User user){
        User loadUserByUsername = userMapper.loadUserByUsername(user.getUsername());
        if (loadUserByUsername != null){
            return 1;
        }
        //插入用户，插入之前先对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        long result = userMapper.reg(user);
        //配置用户的角色，默认都是普通用户
        String[] roles = new String[]{"2"};
        int i = rolesMapper.addRoles(roles, user.getId());
        boolean b = i == roles.length && result == 1;
        if (b){
            return 0;
        }else {
            return 2;
        }
    }

    public int updateUserEmail(String email){
        return userMapper.updateUserEmail(email, Util.getCurrentUser().getId());
    }

    public List<User> getUserByNickname(String nickname){
        List<User> list = userMapper.getUserByNickname(nickname);
        return list;
    }

    public List<Role> getAllRole(){
        return userMapper.getAllRole();
    }

    public int updateUserEnabled(Boolean enabled,Long uid){
        return userMapper.updateUserEnabled(enabled, uid);
    }

    public int deleteUserById(Long uid){
        return userMapper.deleteUserById(uid);
    }

    public int updateUserRoles(Long[] rids,Long id){
        int i = userMapper.deleteUserRoleByUid(id);
        return userMapper.setUserRoles(rids, id);
    }

    public User getUserById(Long id){
        return userMapper.getUserById(id);
    }
}
