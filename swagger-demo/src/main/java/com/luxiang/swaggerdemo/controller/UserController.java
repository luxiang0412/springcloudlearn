package com.luxiang.swaggerdemo.controller;

import com.luxiang.swaggerdemo.dao.UserDao;
import com.luxiang.swaggerdemo.util.ResponseResult;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users", produces = "application/json")
@Api(tags = {"User"})
public class UserController {

    @Autowired
    private UserDao userDao;

    @GetMapping("")
    public ResponseResult listall() {
        return ResponseResult.ok(userDao.findAll());
    }

    @PostMapping("/")
    @ApiOperation(value = "创建用户")
    @ApiResponses({
            @ApiResponse(code = 400, message = "请求参数有误"),
            @ApiResponse(code = 401, message = "未授权"),
            @ApiResponse(code = 403, message = "禁止访问"),
            @ApiResponse(code = 404, message = "请求路径不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public ResponseResult create() {
        return ResponseResult.ok("创建成功");
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteOne(@PathVariable Long id) {
        userDao.deleteById(id);
        return ResponseResult.ok("删除成功");
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "使用ID查询用户")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "ID", name = "id", dataType = "Long", paramType = "path", required = true, defaultValue = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "请求参数有误"),
            @ApiResponse(code = 401, message = "未授权"),
            @ApiResponse(code = 403, message = "禁止访问"),
            @ApiResponse(code = 404, message = "请求路径不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public Object getOne() {
        return userDao.findAll();
    }

    @PutMapping("/{id}")
    public Object updateOne() {
        return userDao.findAll();
    }
}
