package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.ServerResponseCode;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 后台品类管理模块
 * 功能介绍：获取品类节点、增加品类节点、修改品类节点名称、获取品类 ID、递归获取子节点 ID
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Resource
    private ICategoryService iCategoryService;

    @Resource
    private IUserService iUserService;

    /**
     * 增加品类节点
     * TODO 不能添加重复的品类
     *
     * @param parentId     品类 id，如果前端没有传递，则默认为根节点 0
     * @param categoryName 品类名称
     * @param session      session 会话
     * @return json
     */
    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(@RequestParam(value = "parentId", defaultValue = "0") int parentId,
                                              String categoryName, HttpSession session) {
        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.NEED_LOGIN.getCode(), ServerResponseCode.NEED_LOGIN.getDesc());
        }

        // 验证是否是管理员
        if (iUserService.checkAdminRole(sessionUser).isSuccess()) {
            return iCategoryService.addCategory(parentId, categoryName);
        }

        return ServerResponse.createByErrorMsg("不是管理员，无权限操作");
    }

    /**
     * 修改品类名字
     *
     * @param categoryId   品类id
     * @param categoryName 品类名称
     * @param session      session 会话
     * @return json
     */
    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategoryName(int categoryId, String categoryName, HttpSession session) {
        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.NEED_LOGIN.getCode(), ServerResponseCode.NEED_LOGIN.getDesc());
        }

        // 验证是否是管理员
        if (iUserService.checkAdminRole(sessionUser).isSuccess()) {
            return iCategoryService.setCategoryName(categoryId, categoryName);
        }

        return ServerResponse.createByErrorMsg("不是管理员，无权限操作");
    }

    /**
     * 获取某一父节点下的所有平级子节点
     *
     * @param parentId 父节点id，默认为根节点
     * @return json
     */
    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getCategory(@RequestParam(value = "categoryId", defaultValue = "0") int categoryId,
                                                      HttpSession session) {
        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.NEED_LOGIN.getCode(), ServerResponseCode.NEED_LOGIN.getDesc());
        }

        // 验证是否是管理员
        if (iUserService.checkAdminRole(sessionUser).isSuccess()) {
            return iCategoryService.getCategory(categoryId);
        }

        return ServerResponse.createByErrorMsg("不是管理员，无权限操作");
    }

    /**
     * 递归获取子节点编号，即返回所有后代节点（包括自己）
     *
     * @param categoryId 品类id
     * @param session    session 会话
     * @return json
     */
    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(int categoryId, HttpSession session) {
        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.NEED_LOGIN.getCode(), ServerResponseCode.NEED_LOGIN.getDesc());
        }

        // 验证是否是管理员
        if (iUserService.checkAdminRole(sessionUser).isSuccess()) {
            return iCategoryService.getDeepCategory(categoryId);
        }

        return ServerResponse.createByErrorMsg("不是管理员，无权限操作");
    }

}
