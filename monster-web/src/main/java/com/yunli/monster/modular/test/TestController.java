package com.yunli.monster.modular.test;

import com.yunli.monster.core.properties.MqProperties;
import com.yunli.monster.exception.MyException;
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
        return "试试";
    }

    @RequestMapping("1")
    public String test1() {
        if (true) {
            throw new MyException("就是错了");
        }
        return Integer.toString(7 / 0);
    }

    static class o {
        static       int a1 = 5;
        static final int a2 = 5;
        static final int a3 = new Integer(5);
    }

    public static void main(String[] args) {
        System.out.println(TestController.o.a1);
        System.out.println(TestController.o.a2);
        System.out.println(TestController.o.a3);
    }
}

