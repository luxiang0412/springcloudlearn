package com.luxiang.eurekaclient1.controller;

import com.luxiang.eurekaclient1.call.RemoteCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RemoteCall remoteCall;

    @RequestMapping(value = "/insert")
    public void insert(@RequestParam(value = "name", required = false) String name) {
        logger.info("insert name: {}", name);
    }

    @RequestMapping("/list")
    public Object list() {
        logger.info("list:");
        return new Object[]{"list"};
    }

    @RequestMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {

        logger.info("delete by id:{}", id);
    }

    @RequestMapping(value = "/remote/insert")
    public void remoteinsert(@RequestParam(value = "name", required = false) String name) {
        remoteCall.insert(name);
    }

    @RequestMapping("/remote/list")
    public Object remotelist() {
        return remoteCall.list();
    }

    @RequestMapping("/remote/delete/{id}")
    public void remotelist(@PathVariable Long id) {
        remoteCall.delete(id);
    }
}
