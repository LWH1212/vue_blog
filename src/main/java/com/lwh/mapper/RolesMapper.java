package com.lwh.mapper;

import com.lwh.bean.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RolesMapper {

    int addRoles(@Param("roles") String[] roles,@Param("uid") Long id);

    List<Role> getRolesByUid(Long uid);

}
