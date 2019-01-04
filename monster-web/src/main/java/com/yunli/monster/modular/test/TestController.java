package com.yunli.monster.modular.test;

import com.yunli.monster.core.properties.MqProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试请求
 *
 * @author zhouchao
 * @create 2019-01-04 15:12
 */
@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    MqProperties mqProperties;

    @RequestMapping()
    public String test() {
        return mqProperties.toString();
    }

}
