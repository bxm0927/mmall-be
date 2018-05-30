package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.ServerResponseCode;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 后台商品管理模块
 * 功能分析：产品搜索、产品列表、产品详情、分页及动态排序、图片上传、富文本上传、商品上下架、增加商品、更新商品
 * - SpringMVC 文件上传
 * - 上传的图片存储到 FTP 文件服务器
 * - mybatis-pagehelper 实现分页及动态排序
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Resource
    private IProductService iProductService;

    @Resource
    private IUserService iUserService;

    @Resource
    private IFileService iFileService;

    /**
     * 新增/更新商品
     *
     * @param product 更新会传 id，新增不会传 id
     * @param session session 会话
     * @return json
     */
    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse<String> save(Product product, HttpSession session) {
        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.NEED_LOGIN.getCode(), "用户未登录，请先登录");
        }

        // 验证是否是管理员
        if (iUserService.checkAdminRole(sessionUser).isSuccess()) {
            return iProductService.save(product);
        }

        return ServerResponse.createByErrorMsg("不是管理员，无权限操作");
    }

    /**
     * 商品上下架（更新商品状态 status）
     *
     * @param productId 商品id
     * @param status    商品状态.1-在售 2-下架 3-删除
     * @param session   session 会话
     * @return json
     */
    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status, HttpSession session) {

        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.NEED_LOGIN.getCode(), "用户未登录，请先登录");
        }

        // 验证是否是管理员
        if (iUserService.checkAdminRole(sessionUser).isSuccess()) {
            return iProductService.setSaleStatus(productId, status);
        }

        return ServerResponse.createByErrorMsg("不是管理员，无权限操作");
    }

    /**
     * 获取商品详情
     *
     * @param productId 商品id
     * @param session   session 会话
     * @return json
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId, HttpSession session) {
        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.NEED_LOGIN.getCode(), "用户未登录，请先登录");
        }

        // 验证是否是管理员
        if (iUserService.checkAdminRole(sessionUser).isSuccess()) {
            return iProductService.manageProductDetail(productId);
        }

        return ServerResponse.createByErrorMsg("不是管理员，无权限操作");
    }

    /**
     * 获取商品列表
     * 利用 PageHelper 完成分页功能
     *
     * @param pageNum  页码，默认第1页
     * @param pageSize 页容量，默认一页10条记录
     * @param session  session 会话
     * @return json
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getProductList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                   HttpSession session) {

        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.NEED_LOGIN.getCode(), "用户未登录，请先登录");
        }

        // 验证是否是管理员
        if (iUserService.checkAdminRole(sessionUser).isSuccess()) {
            return iProductService.manageProductList(pageNum, pageSize);
        }

        return ServerResponse.createByErrorMsg("不是管理员，无权限操作");
    }

    /**
     * 商品搜索
     * 利用 PageHelper 完成分页功能
     *
     * @param productName 按商品名称查询
     * @param productId   按商品id查询
     * @param pageNum     页码
     * @param pageSize    页容量
     * @param session     session 会话
     * @return json
     */
    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(String productName, Integer productId,
                                           @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                           HttpSession session) {

        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.NEED_LOGIN.getCode(), "用户未登录，请先登录");
        }

        // 验证是否是管理员
        if (iUserService.checkAdminRole(sessionUser).isSuccess()) {
            return iProductService.search(productName, productId, pageNum, pageSize);
        }

        return ServerResponse.createByErrorMsg("不是管理员，无权限操作");
    }

    /**
     * SpringMVC 文件上传
     *
     * @param request request 请求创建 Servlet 上下文路径
     * @param session session 会话
     * @param file    SpringMVC 文件上传类 MultipartFile
     * @return json
     */
    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse<Map<String, String>> upload(HttpServletRequest request, HttpSession session,
                                                      @RequestParam(value = "upload_file", required = false) MultipartFile file) {

        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.NEED_LOGIN.getCode(), "用户未登录，请先登录");
        }

        // 验证是否是管理员
        if (iUserService.checkAdminRole(sessionUser).isSuccess()) {
            // SpringMVC 文件上传相关逻辑
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.uploadFile(file, path);

            // 构建图片地址
            // String imageUrl = path + targetFileName; // Tomcat
            String imageUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map<String, String> map = Maps.newHashMap();
            map.put("imageUri", targetFileName);
            map.put("imageUrl", imageUrl);

            return ServerResponse.createBySuccessData(map);
        }

        return ServerResponse.createByErrorMsg("不是管理员，无权限操作");
    }

    /**
     * 富文本图片上传
     * SpringMVC 文件上传
     * 返回 Simditor 富文本编辑器需要的格式，参考：http://simditor.tower.im/docs/doc-config.html
     * <pre>
     * {
     *    "success": true/false,
     *    "msg": "error message",
     *    "file_path": "[real file path]"
     * }
     * </pre>
     *
     * @param request  request 请求
     * @param response response 响应，用于修改 HTTP 响应头
     * @param session  session 会话
     * @param file     SpringMVC 文件上传类
     * @return json
     */
    @RequestMapping(value = "richtext_img_upload.do")
    @ResponseBody
    public Map<String, Object> richtextImgUpload(HttpServletRequest request, HttpServletResponse response,
                                                 HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file) {

        Map<String, Object> map = Maps.newHashMap();

        // 验证是否登录，如果没有，则强制登录（status=10）
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null) {
            map.put("success", false);
            map.put("msg", "用户未登录，请先登录");
            return map;
        }

        // 验证是否是管理员
        if (iUserService.checkAdminRole(sessionUser).isSuccess()) {
            // SpringMVC 文件上传相关逻辑
            String path = request.getSession().getServletContext().getRealPath("upload"); // webapp/upload
            String targetFileName = iFileService.uploadFile(file, path);

            if (StringUtils.isBlank(targetFileName)) {
                map.put("success", false);
                map.put("msg", "上传失败");
                return map;
            }

            // 构建图片地址
            // String imageUrl = path + targetFileName; // Tomcat
            String imageUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            map.put("success", true);
            map.put("msg", "上传成功");
            map.put("file_path", imageUrl);

            response.setHeader("Access-Control-Allow-Headers", "X-File-Name"); // 修改响应头

            return map;
        }

        map.put("success", false);
        map.put("msg", "不是管理员，无权限操作");
        return map;
    }

}
