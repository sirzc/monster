package com.yunli.monster;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class AppTest {

    @Test
    public void shouldAnswerWithTrue() {
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread() {
                AtomicLong a = new AtomicLong(0);
                long b = 0;

                @Override
                public void run() {
                    System.err.println(a.get() + ":" + b);
                    a.getAndIncrement();
                    b++;
                }
            };
            thread.start();
        }
    }

    @Test
    public void hashCodeTest() {
        Set<Object> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            s.add(new String("test"));
        }
        System.err.println(s.size());
        s.clear();

        for (int i = 0; i < 10; i++) {
            s.add(new V(1));
        }
        System.out.println(s.size());
    }
}
