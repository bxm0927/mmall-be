package com.mmall.controller.portal;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.ServerResponseCode;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

/**
 * 前台用户管理模块
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService iUserService;

    /**
     * 用户登录
     * 指定 method 为 POST，则只能通过 POST 方式请求，如果不指定，则可以以任意方式请求
     * 生产环境需要以 POST 方式请求，开发环境用 GET 方便调试
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

        // 如果登陆成功，则将该用户存到 session，即保存用户状态
        if (res.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, res.getData());
        }

        return res;
    }

    /**
     * 退出登录
     * TODO 添加未登录状态下退出报错功能
     *
     * @param session session 会话
     * @return json
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {

        // 清除 session，即清除用户状态
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccessMsg("退出登录成功");
    }

    /**
     * 注册
     * TODO 注册成功向用户邮箱发送邮件
     *
     * @param user 用户
     * @return json
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {

        return iUserService.register(user);
    }

    /**
     * 校验用户名和密码是否有效，用于前端实时校验
     *
     * @param str  str可以是用户名也可以是邮箱
     * @param type 对应的type是"username"和"email"，判断是用户名还是邮箱
     * @return json
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {

        return iUserService.checkValid(str, type);
    }

    /**
     * 获取登录用户信息
     *
     * @param session session 会话
     * @return json
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccessData(user);
        }

        return ServerResponse.createByErrorMsg("用户未登录，无法获取当前用户的信息");
    }

    /**
     * 忘记密码，返回密码提示问题
     *
     * @param username 请输入用户名
     * @return json
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {

        return iUserService.forgetGetQuestion(username);
    }

    /**
     * 校验密码提示问题的答案
     * 正确的返回值里面有一个token
     *
     * @param username 用户名
     * @param question 密码提示问题
     * @param answer   密码提示答案
     * @return json
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {

        return iUserService.forgetCheckAnswer(username, question, answer);
    }

    /**
     * 忘记密码的重置密码（未登录状态下的修改密码）
     * Token 认证：防止横向越权，例如接口为 localhost:8080/user/forget_reset_password.do?username=xxx&passwordNew=xxx&forgetToken=xxx，
     * 如果不使用 Token 认证，那么用户可以随意修改他人的密码。
     * 只有在正确的回答了密码提示问题、密码提示答案才能获取 Token（上面的接口）
     * 同时，Token 是存在有效期的，这里使用缓存处理。例如有效期设为30分钟，那么即使有人截获了你的 token，当他在使用时也有可能是过期的
     *
     * @param username    用户名
     * @param passwordNew 新密码
     * @param forgetToken Token 认证
     * @return json
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {

        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登录状态下的修改密码
     *
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @param session     session 会话
     * @return json
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户未登录");
        }

        return iUserService.resetPassword(user, passwordOld, passwordNew);
    }

    /**
     * 登录状态下更新个人信息
     * id 和 username 是不能被更新的，可以更新：email、phone、question、answer
     *
     * @param user    用户
     * @param session session 会话
     * @return json
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(User user, HttpSession session) {

        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorMsg("用户未登录");
        }

        user.setId(sessionUser.getId());
        user.setUsername(sessionUser.getUsername());
        ServerResponse<User> res = iUserService.updateInformation(user);

        // 如果更新成功，则将新的用户状态存到 session
        if (res.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, res.getData());
        }

        return res;
    }

    /**
     * 获取当前登录用户的详细信息，并强制登录
     *
     * @param session session 会话
     * @return json
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session) {

        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.NEED_LOGIN.getCode(), "用户未登录，请先登录");
        }

        return iUserService.getInformation(sessionUser.getId());
    }

}
