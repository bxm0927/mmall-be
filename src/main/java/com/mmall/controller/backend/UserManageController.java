package com.mmall.controller.backend;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

/**
 * 后台用户管理模块
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Resource
    private IUserService iUserService;

    /**
     * 后台管理员登录
     * TODO 管理员账号既可以登录前台又可以后台？
     *
     * @param username 用户名
     * @param password 密码
     * @param session  session 会话
     * @return json
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {

        ServerResponse<User> res = iUserService.login(username, password);

        // 如果登陆成功，则校验是否是管理员，如果是，则将该用户存到 session
        if (res.isSuccess()) {
            User user = res.getData();

            if (Const.Role.ROLE_ADMIN == user.getRole()) {
                session.setAttribute(Const.CURRENT_USER, user);
                return res;
            } else {
                return ServerResponse.createByErrorMsg("不是管理员，无权限登陆");
            }
        }

        return res;
    }

    // TODO 后台管理员注册
    // TODO 后台管理员找回密码
    // TODO 用户管理模块

}
