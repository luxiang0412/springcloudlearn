package com.luxiang.eurekaclient1.call;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "eureka-client1")
public interface RemoteCall {

    @RequestMapping(value = "/insert")
    public void insert(@RequestParam(value = "name", required = false) String name);

    @RequestMapping("/list")
    public Object list();

    @RequestMapping("/delete/{id}")
    public void delete(@PathVariable Long id);
}
