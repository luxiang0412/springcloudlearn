package com.luxiang.rocketmq.model;

/**
 * @author luxiang
 * description  //角色
 * create       2021-05-11 11:56
 */
public class Role extends L1{

    private Long id;

    private String code;

    private String name;

    private Integer deleted;

    private Long parentid;

    private Integer roleType;

    private String othersystemDataInserttime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Long getParentid() {
        return parentid;
    }

    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public String getOthersystemDataInserttime() {
        return othersystemDataInserttime;
    }

    public void setOthersystemDataInserttime(String othersystemDataInserttime) {
        this.othersystemDataInserttime = othersystemDataInserttime;
    }

    public Role(Long id, String code, String name, Integer deleted, Long parentid, Integer roleType, String othersystemDataInserttime) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.deleted = deleted;
        this.parentid = parentid;
        this.roleType = roleType;
        this.othersystemDataInserttime = othersystemDataInserttime;
    }

    public Role() {
    }
}
