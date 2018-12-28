package com.yunli.monster;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

public class AppTest {

    @Test
    public void shouldAnswerWithTrue() {
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(){
                AtomicLong a = new AtomicLong(0);
                long b = 0;
                @Override
                public  void run(){
                    System.err.println(a.get() + ":" + b);
                    a.getAndIncrement();
                    b++;
                }
            };
            thread.start();
        }
    }
}
