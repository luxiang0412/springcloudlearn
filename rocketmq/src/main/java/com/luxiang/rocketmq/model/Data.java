package com.luxiang.rocketmq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author luxiang
 * description  //data
 * create       2021-05-11 12:09
 */
public class Data{

    @JsonProperty("tableName")
    private String tableName;

    private List<? extends L1> data;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<? extends L1> getData() {
        return data;
    }

    public void setData(List<? extends L1> data) {
        this.data = data;
    }

    public Data(String tableName, List<? extends L1> data) {
        this.tableName = tableName;
        this.data = data;
    }
}
