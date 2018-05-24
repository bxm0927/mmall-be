package com.mmall.service.impl;

import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.ServerResponseCode;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public ServerResponse<String> checkValid(String str, String type) {

        // 如果传入了检验类型 type
        if (StringUtils.isNotBlank(type)) {
            // 校验用户名是否存在
            if (Const.LoginType.USERNAME.equals(type)) {
                int rows = userMapper.checkUsername(str);
                if (rows > 0) {
                    return ServerResponse.createByErrorMsg("用户名已存在");
                }
            }

            // 校验邮箱是否存在
            if (Const.LoginType.EMAIL.equals(type)) {
                int rows = userMapper.checkEmail(str);
                if (rows > 0) {
                    return ServerResponse.createByErrorMsg("邮箱已存在");
                }
            }
        } else {
            // 否则返回参数非法错误
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), ServerResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        // 校验成功则说明用户名/邮箱不存在
        return ServerResponse.createBySuccessMsg("校验成功");
    }

    @Override
    public ServerResponse<User> login(String username, String password) {

        // 校验用户名是否存在
        ServerResponse<String> validResponse = this.checkValid(username, Const.LoginType.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMsg("用户名不存在");
        }

        // TODO 目前只支持用户名登录，后期需要增加邮箱登录

        // 校验用户名与密码是否正确，注意：比较的是加密后的密码，而不是原密码
        String safePassword = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, safePassword);
        if (user == null) {
            return ServerResponse.createByErrorMsg("密码错误");
        }

        // 密码置空，不响应密码信息
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccessMsgData("登陆成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {

        // 校验用户名是否存在
        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.LoginType.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse; // 用户名已存在
        }

        // 校验邮箱是否存在
        validResponse = this.checkValid(user.getEmail(), Const.LoginType.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse; // 邮箱已存在
        }

        // 设置角色权限为普通用户、并对密码进行加密
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        // 插入到数据库
        int rows = userMapper.insert(user);
        if (rows == 0) {
            return ServerResponse.createByErrorMsg("注册失败");
        }

        return ServerResponse.createBySuccessMsg("注册成功");
    }

    @Override
    public ServerResponse<String> forgetGetQuestion(String username) {

        // 校验用户名是否存在
        ServerResponse<String> validResponse = this.checkValid(username, Const.LoginType.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMsg("用户名不存在");
        }

        // 通过用户名查找密码提示问题
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccessData(question);
        }

        return ServerResponse.createByErrorMsg("未设置找回密码的密码提示问题");
    }

    @Override
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {

        int rows = userMapper.forgetCheckAnswer(username, question, answer);
        if (rows > 0) {
            // 正确的返回值里面有一个token，用于修改密码之后的重置密码，传递给下一个接口
            // 并且利用缓存来设置 token 的有效期，提高安全性
            String token = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, token);

            return ServerResponse.createBySuccessData(token);
        }

        return ServerResponse.createByErrorMsg("密码提示问题回答错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {

        // 校验用户名是否存在
        ServerResponse<String> validResponse = this.checkValid(username, Const.LoginType.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMsg("用户名不存在");
        }

        // 校验 token 是否传递
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数非法，需要传递 token");
        }

        // 校验缓存中的 token 是否有效
        String tokenCache = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(tokenCache)) {
            return ServerResponse.createByErrorMsg("token 无效或已经过期");
        }

        // 对比缓存中的 token，成功则修改密码
        if (StringUtils.equals(forgetToken, tokenCache)) {
            String safePassword = MD5Util.MD5EncodeUtf8(passwordNew);
            int rows = userMapper.updatePasswordByUsername(username, safePassword);

            if (rows > 0) {
                return ServerResponse.createBySuccessMsg("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMsg("token 错误，请重新获取重置密码的 token");
        }

        return ServerResponse.createByErrorMsg("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew) {

        // 校验旧密码是否正确，带上 id 查询是为了防止横向越权漏洞
        String safePassword = MD5Util.MD5EncodeUtf8(passwordOld);
        int rows = userMapper.checkPassword(user.getId(), safePassword);
        if (rows == 0) {
            return ServerResponse.createByErrorMsg("修改密码失败，旧密码输入错误");
        }

        // 插入新密码
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        rows = userMapper.updateByPrimaryKeySelective(user); // 有选择的更新
        if (rows > 0) {
            return ServerResponse.createBySuccessMsg("修改密码成功");
        }

        return ServerResponse.createByErrorMsg("修改密码失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {

        // 校验邮箱是否已被他人占用
        int rows = userMapper.checkEmailById(user.getId(), user.getEmail());
        if (rows > 0) {
            return ServerResponse.createByErrorMsg("该邮箱已被他人占用，请更换邮箱后重试");
        }

        rows = userMapper.updateByPrimaryKeySelective(user); // 有选择的更新
        if (rows > 0) {
            User updateUser = userMapper.selectByPrimaryKey(user.getId());
            // 密码置空，不响应密码信息
            updateUser.setPassword(StringUtils.EMPTY);

            return ServerResponse.createBySuccessMsgData("更新个人信息成功", updateUser);
        }

        return ServerResponse.createByErrorMsg("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer id) {

        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }

        // 密码置空，不响应密码信息
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccessData(user);
    }

    // ************************** 上面是前台接口 portal ↑ 下面是后台接口 backend ↓ **************************//

    @Override
    public ServerResponse<User> checkAdminRole(User user) {

        if (Const.Role.ROLE_ADMIN == user.getRole()) {
            return ServerResponse.createBySuccess();
        }

        return ServerResponse.createByError();
    }

}
