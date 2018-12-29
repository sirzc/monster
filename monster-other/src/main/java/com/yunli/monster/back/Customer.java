package com.yunli.monster.back;

/**
 * 顾客
 *
 * @author zhouchao
 * @create 2018-12-29 11:02
 */
public class Customer implements Back {
    /**
     * 顾客住店
     */
    private Hotel hotel;

    private String name;

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Customer{" + "hotel=" + hotel + ", name='" + name + '\'' + '}';
    }

    /**
     * 预约操作
     */
    public void reserve() {
        System.err.println("开始预约，将身份信息传输给酒店");
        hotel.reservationService(this);
    }

    @Override
    public void callBack(boolean success) {
        System.err.println("预约成功");
    }

    public static void main(String[] args) {
        // 新建一个用户
        Customer customer = new Customer();
        // 用户选择了一家酒店
        customer.setHotel(new Hotel());
        customer.setName("23");
        // 用户预约这个酒店,预约是否成功，返回给用户
        customer.reserve();

    }
}
