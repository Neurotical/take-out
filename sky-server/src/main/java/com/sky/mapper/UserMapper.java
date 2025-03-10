package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openId
     * @return
     */
    @Select("select * from sky_take_out.user where openid = #{openId}")
    User getUserByOpenId(String openId);

    void insert(User user);

    @Select("select * from sky_take_out.user where id=#{id}")
    User getById(Long userId);

    Integer getByMap(Map map);
}
