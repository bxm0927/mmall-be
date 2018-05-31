package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    // 校验用户名是否存在
    int checkUsername(String username);

    // 校验邮箱是否存在
    int checkEmail(String email);

    // 校验用户名与密码是否正确
    // mybatis 在传入多个参数时，需要使用 @Param 注解
    User selectLogin(@Param("username") String username, @Param("password") String password);

    // 忘记密码，通过用户名查找提示问题
    String selectQuestionByUsername(String username);

    // 校验提交问题答案
    int forgetCheckAnswer(@Param("username") String username, @Param("question") String question,
                          @Param("answer") String answer);

    // 根据用户名更新密码，用于忘记密码的重设密码
    int updatePasswordByUsername(@Param("username") String username, @Param("passwordNew") String passwordNew);

    // 校验该旧密码是否正确
    int checkPassword(@Param("id") Integer id, @Param("passwordOld") String passwordOld);

    // 校验邮箱是否已被他人占用
    int checkEmailById(@Param("id") Integer id, @Param("email") String email);

}
