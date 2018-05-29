package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.common.ServerResponseCode;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<String> addCategory(int parentId, String categoryName) {
        if (StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数非法");
        }

        // 构建新的品类节点
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true); // 这个分类是可用的

        int rows = categoryMapper.insert(category);
        if (rows > 0) {
            return ServerResponse.createBySuccessMsg("添加品类成功");
        }

        return ServerResponse.createByErrorMsg("添加品类失败");
    }

    @Override
    public ServerResponse<String> setCategoryName(int categoryId, String categoryName) {
        if (StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数非法");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rows = categoryMapper.updateByPrimaryKeySelective(category);
        if (rows > 0) {
            return ServerResponse.createBySuccessMsg("修改品类成功");
        }

        return ServerResponse.createByErrorMsg("修改品类失败");
    }

    @Override
    public ServerResponse<List<Category>> getCategory(int categoryId) {
        // 查询 parentId 相同的品类
        List<Category> list = categoryMapper.selectByParentId(categoryId);
        return ServerResponse.createBySuccessData(list);
    }

    @Override
    public ServerResponse<List<Integer>> getDeepCategory(int categoryId) {
        Set<Category> set = Sets.newHashSet();
        getChildCategory(set, categoryId); // 对象的引用传递，会改变对象

        List<Integer> list = Lists.newArrayList();
        for (Category item : set) {
            list.add(item.getId());
        }

        return ServerResponse.createBySuccessData(list);
    }

    /**
     * 递归算法，获取子节点
     * 对象的引用传递，会改变对象
     *
     * @param set        Set 自动排重，需要重写 equals 和 hashcode 方法
     * @param categoryId 品类id
     */
    private void getChildCategory(Set<Category> set, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            set.add(category); // 先把自己放进去
        }

        List<Category> list = categoryMapper.selectByParentId(categoryId);
        for (Category item : list) {
            getChildCategory(set, item.getId()); // 再把子节点挨个放进去
        }
    }

}
