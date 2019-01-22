package com.yunli.mq.common;

/**
 * 配置文件属性名 常量池
 *
 * @author zhouchao
 * @date 2019-01-16 16:47
 */
public class MqConstant {

    /**
     * 配置文件根路径
     */
    public static final String DEFAULT_ROOT_DIR = "mq";

    /**
     * 默认生产者配置文件名
     */
    public static final String DEFAULT_PRODUCER_FILE = "mq_producer.properties";

    /**
     * 默认消费者配置文件名
     */
    public static final String DEFAULT_CONSUMER_FILE = "mq_consumer.properties";
    /**
     * 配置文件默认前缀
     */
    public static final String DEFAULT_MQ_PREFIX     = "default";

    /*********************************【生产者默认配置】******************************/
    
    /**
     * -服务地址：
     * -客户端实例名称：客户端创建的多个Producer、 Consumer实际是共用一个内部实例（这个实例包含网络连接、线程资源等）
     * -消息队列数：
     * -超时时间：发送消息超时.默认是3000毫秒
     * -集群名称：
     */
    public static final String MQ_PRODUCER_NAMESERVER        = ".mq.producer.nameserver";
    public static final String MQ_PRODUCER_INSTANCENAME      = ".mq.producer.instancename";
    public static final String MQ_PRODUCER_TOPICQUEUENUMS    = ".mq.producer.topicqueuenums";
    public static final String MQ_PRODUCER_SENDTIMEOUTMILLIS = ".mq.producer.sendtimeoutmillis";
    public static final String MQ_PRODUCER_GROUPNAME         = ".mq.producer.groupname";

    /*********************************【消费者默认配置】***************************** */

    /**
     * 消费者实现接口
     */
    public static final String CONSUMER_INTERFACE_CLASSNAME  = "com.unitop.mq.customer.handler.IMqHandler";
    /**
     * 消费者默认扫描包路径
     */
    public static final String CONSUMER_DEFAULT_SCAN_PACKAGE = "com.unitop";
    /**
     * 消费者集群名称分隔符
     */
    public static final String CONSUMER_GROUPNAME_SEP        = "_";
    /**
     * 顺序模式标志
     */
    public static final String CONSUMER_ORDERLY_TRUE         = "orderly";
    /**
     * 广播模式标志
     */
    public static final String CONSUMER_BROADCAST_TRUE       = "broadcast";

    /**
     * 消费者：
     * -服务地址：
     * -客户端实例名称：客户端创建的多个Producer、 Consumer实际是共用一个内部实例（这个实例包含网络连接、线程资源等）
     * -阻塞时间：消息可能阻止使用线程的最长时间（以分钟为单位,默认：15）。
     * -扫描包路径：如果不配置默认扫描com.unitop
     */
    public static final String MQ_CONSUMER_NAMESERVER   = ".consumer.nameserver";
    public static final String MQ_CONSUMER_INSTANCENAME = ".consumer.instancename";
    public static final String MQ_CONSUMER_TIMEOUT      = ".consumer.consumertimeoutminutes";
    public static final String MQ_CONSUMER_SCAN_PACKAGE = ".consumer.scan.package";

}
