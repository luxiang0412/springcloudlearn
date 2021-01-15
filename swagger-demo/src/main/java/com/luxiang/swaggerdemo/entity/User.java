package com.luxiang.swaggerdemo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@ApiModel(description = "用户Model")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(value = "用户ID", name = "id")

    private Long id;
    @Column
    @ApiModelProperty(value = "用户名", name = "username", required = true, example = "luxiang")
    private String username;

    @Column
    @ApiModelProperty(value = "密码", name = "password", required = true, example = "luxiang1234.")
    private String password;
}
