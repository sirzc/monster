package com.yunli.monster;

/**
 * @author zhouchao
 * @create 2019-01-02 17:19
 */
public class V {
    int i;
    public V(int i) {
        this.i = i;
    }
    public int getI() {
        return this.i;
    }
    public boolean equals(Object o) {
        V v = (V) o;
        System.out.print("hashcode相同的，然后才执行的equals()方法的!");
        System.out.println(v.getI() == this.i);
        return v.getI() == this.i;
    }
    //如果不重写，将会产生不同的hashcode,所以可以加进set里面
    public int hashCode() {
        System.out.println("先执行hashCode()方法的!");
        return i;
    }
}
