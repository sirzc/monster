package com.yunli.mq.customer.annotation;

import java.lang.annotation.*;

/**
 * unitop-mq 消费者数据元注解
 *
 * @author zhouchao
 * @date 2019-01-18 15:09
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqConsumerMeta {

    /**
     * 订阅主题
     * @return
     */
    String topic();

    /**
     * 子表达式（暂时不使用）
     * @return
     */
    String subExpression() default "*";

    /**
     * 引用配置文件前缀（切换配置）
     * @return
     */
    String prefix() default "default";

    /**
     * 分组，消息按组进行消费
     * @return
     */
    String group();

    /**
     * 消息是否为有序消息
     * @return
     */
    boolean isOrderly() default false;

    /**
     * 消息是否广播接收
     * @return
     */
    boolean isBroadcast() default false;

    /**
     * 消费者最小线程
     * @return
     */
    int consumerThreadCountMin() default 10;

    /**
     * 消费者最大线程
     * @return
     */
    int consumerThreadCountMax() default 30;

    /**
     * 每次推送消息数量
     * @return
     */
    int consumeMessageBatchMaxSize() default 1;
}
