package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     *
     * @param openid
     * @return
     */
@Select("select * from sky_take_out.user where openid=#{openid}")
    User getByOpenId(String openid);
    /**
     * 添加用户
     *
     * @param user
     * @return
     */
@AutoFill(value = OperationType.INSERT)
    void insert(User user);
}
