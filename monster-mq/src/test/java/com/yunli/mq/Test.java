package com.yunli.mq;

import java.util.Random;

/**
 * @author zhouchao
 * @date 2019-02-20 16:38
 */
public class Test {

    private static Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        System.err.println(random.nextInt(4));
    }

}
