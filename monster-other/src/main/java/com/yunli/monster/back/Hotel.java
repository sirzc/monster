package com.yunli.monster.back;

/**
 * @author zhouchao
 * @create 2018-12-29 11:04
 */
public class Hotel {

    /**
     * 预约服务
     * @param customer
     */
    public void reservationService(Back customer) {
        System.err.println("酒店预约服务收到用户信息");
        System.err.println(customer.toString());
        customer.callBack(true);
    }
}
