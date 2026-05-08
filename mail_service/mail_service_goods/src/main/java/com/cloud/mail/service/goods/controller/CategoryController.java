package com.cloud.mail.service.goods.controller;

import com.cloud.mail.service.goods.model.Category;
import com.cloud.util.RespResult;
import com.cloud.mail.service.goods.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分类管理", description = "商品分类增删改查")
@RestController
@RequestMapping("/goods/Category")
public class CategoryController {

    private final CategoryService categoryService;
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "新增分类")
    @PutMapping
    public RespResult<String> add(@RequestBody Category category) {
        categoryService.save(category);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "更新分类")
    @PostMapping
    public RespResult<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public RespResult<String> delete(@Parameter(description = "分类ID") @PathVariable Integer id) {
        categoryService.removeById(id);
        return RespResult.ok("操作成功");
    }

    @Operation(summary = "根据ID查询分类")
    @GetMapping("/{id}")
    public RespResult<Category> getById(@Parameter(description = "分类ID") @PathVariable Integer id) {
        return RespResult.ok(categoryService.getById(id));
    }

    @Operation(summary = "查询全部分类")
    @GetMapping("/list")
    public RespResult<List<Category>> list() {
        return RespResult.ok(categoryService.list());
    }

    @Operation(summary = "根据父ID查询子分类")
    @RequestMapping("/parent/{id}")
    public RespResult<List<Category>> findByParentId(@Parameter(description = "父分类ID") @PathVariable("id") Integer parentId) {
        return RespResult.ok(categoryService.findByParentId(parentId));
    }
}
