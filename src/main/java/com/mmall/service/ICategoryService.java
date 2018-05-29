package com.mmall.service;

import java.util.List;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

public interface ICategoryService {

    ServerResponse<String> addCategory(int parentId, String categoryName);

    ServerResponse<String> setCategoryName(int categoryId, String categoryName);

    ServerResponse<List<Category>> getCategory(int categoryId);

    ServerResponse<List<Integer>> getDeepCategory(int categoryId);

}
