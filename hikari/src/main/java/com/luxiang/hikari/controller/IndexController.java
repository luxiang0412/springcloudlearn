package com.luxiang.hikari.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author luxiang
 * description  //index
 * create       2021-05-12 07:35
 */
@RestController
public class IndexController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public void index(String t) {
        StringBuilder sql = new StringBuilder();
        if (t != null && t.equals("all")) {
            sql.append("select * from test_migrate_4_l1");
        } else {
            sql.append("select levelclass, healthstatus from test_migrate_4_l1");
        }
        long end = System.currentTimeMillis() + 600000L;
        while (System.currentTimeMillis() < end) {
            List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString());
        }
    }

    @GetMapping("/test")
    public void test() {
        for (int i = 0; i < 50; i++) {
            System.out.println(String.format("第%s次开始...", i + 1));
            StringBuilder sql = new StringBuilder();
            sql.append("ALTER TABLE test_migrate_4_l1 ADD COLUMN sys_id int8 NOT NULL DEFAULT shard_1.id_generator (),\n" +
                    "    ADD COLUMN sys_mark VARCHAR (1024) DEFAULT NULL,\n" +
                    "    ADD COLUMN sys_create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    ADD COLUMN sys_create_id VARCHAR (64) NOT NULL DEFAULT 'flink',\n" +
                    "    ADD COLUMN sys_update_time TIMESTAMP DEFAULT NULL,\n" +
                    "    ADD COLUMN sys_update_id VARCHAR (64) DEFAULT NULL,\n" +
                    "    ADD COLUMN sys_is_delete int8 NOT NULL DEFAULT 0,\n" +
                    "    ADD COLUMN sys_data_trace_code VARCHAR (255) NOT NULL DEFAULT '123456789';\n" +
                    "\n" +
                    "COMMENT ON COLUMN test_migrate_4_l1.sys_id IS '主键ID';\n" +
                    "\n" +
                    "COMMENT ON COLUMN test_migrate_4_l1.sys_mark IS '备注';\n" +
                    "\n" +
                    "COMMENT ON COLUMN test_migrate_4_l1.sys_create_time IS '创建时间';\n" +
                    "\n" +
                    "COMMENT ON COLUMN test_migrate_4_l1.sys_create_id IS '创建用户ID';\n" +
                    "\n" +
                    "COMMENT ON COLUMN test_migrate_4_l1.sys_update_time IS '修改时间';\n" +
                    "\n" +
                    "COMMENT ON COLUMN test_migrate_4_l1.sys_update_id IS '修改用户ID';\n" +
                    "\n" +
                    "COMMENT ON COLUMN test_migrate_4_l1.sys_is_delete IS '是否删除';\n" +
                    "\n" +
                    "COMMENT ON COLUMN test_migrate_4_l1.sys_data_trace_code IS '追溯码';\n" +
                    "\n" +
                    "ALTER TABLE test_migrate_4_l1 ALTER COLUMN sys_id\n" +
                    "                               SET DEFAULT NULL,\n" +
                    "                           ALTER COLUMN sys_mark\n" +
                    "                               SET DEFAULT NULL,\n" +
                    "                           ALTER COLUMN sys_create_time\n" +
                    "                               SET DEFAULT NULL,\n" +
                    "                           ALTER COLUMN sys_create_id\n" +
                    "                               SET DEFAULT NULL,\n" +
                    "                           ALTER COLUMN sys_update_time\n" +
                    "                               SET DEFAULT NULL,\n" +
                    "                           ALTER COLUMN sys_update_id\n" +
                    "                               SET DEFAULT NULL,\n" +
                    "                           ALTER COLUMN sys_is_delete\n" +
                    "                               SET DEFAULT NULL,\n" +
                    "                           ALTER COLUMN sys_data_trace_code\n" +
                    "                               SET DEFAULT NULL;\n" +
                    "\n" +
                    "ALTER TABLE test_migrate_4_l1\n" +
                    "SET distributed BY (sys_id, sys_is_delete);\n" +
                    "\n" +
                    "ALTER TABLE test_migrate_4_l1 ADD CONSTRAINT test_migrate_4_l1_pkey PRIMARY KEY (sys_id, sys_is_delete);");
            jdbcTemplate.execute(sql.toString());
            sql.setLength(0);
            sql.append("ALTER TABLE test_migrate_4_l1 DROP COLUMN sys_id,\n" +
                    " DROP COLUMN sys_mark,\n" +
                    " DROP COLUMN sys_create_time,\n" +
                    " DROP COLUMN sys_create_id,\n" +
                    " DROP COLUMN sys_update_time,\n" +
                    " DROP COLUMN sys_update_id,\n" +
                    " DROP COLUMN sys_is_delete,\n" +
                    " DROP COLUMN sys_data_trace_code;");
            jdbcTemplate.execute(sql.toString());
            System.out.println(String.format("第%s次结束...", i + 1));
        }
    }
}
