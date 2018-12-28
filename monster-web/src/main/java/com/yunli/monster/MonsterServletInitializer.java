package com.yunli.monster;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * monster Servlet启动实例(web)
 *
 * @author zhouchao
 * @create 2018-12-25 14:25
 */
public class MonsterServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MonsterApplication.class);
    }
}
