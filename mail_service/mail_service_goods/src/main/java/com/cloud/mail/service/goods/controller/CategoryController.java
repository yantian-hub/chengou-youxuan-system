package com.cloud.mail.service.goods.controller;


import com.cloud.mail.service.goods.model.Category;
import com.cloud.util.RespResult;
import com.cloud.mail.service.goods.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods/Category")
public class CategoryController {

    private final CategoryService categoryService;
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    //根据分类父id查询分类
    @RequestMapping("/parent/{id}")
    public RespResult<List<Category>> findByParentId(@PathVariable("id") Integer parentId) {
        return RespResult.ok(categoryService.findByParentId(parentId));
    }
}
