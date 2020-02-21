package com.luxiang.swaggerdemo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@Entity
@ApiModel(description = "用户Model")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Null(message = "id必须为空")
    @ApiModelProperty(value = "用户ID", name = "id")

    private Long id;
    @Column
    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", name = "username", required = true, example = "xxxx")
    private String username;

    @Column
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", name = "password", required = true, example = "xxxxx")
    private String password;
}
