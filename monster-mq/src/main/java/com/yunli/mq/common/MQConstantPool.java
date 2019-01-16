package com.yunli.mq.common;

/**
 * 配置文件属性名 常量池
 *
 * @author zhouchao
 * @date 2019-01-16 16:47
 */
public class MQConstantPool {

    /**
     * 配置文件根路径
     */
    public static final String ROOT_DIR = "";

    /**
     * 默认生产者配置文件名
     */
    public static final String DEFAULT_PRODUCER_FILE = "unitop_mq_producer.properties";

    /**
     * *******************************【配置文件变量】*****************************
     * 生产者：
     * 服务地址：
     * 客户端实例名称：客户端创建的多个Producer、 Consumer实际是共用一个内部实例（这个实例包含网络连接、线程资源等）
     * 消息队列数：
     * 超时时间：
     * 集群名称：
     */
    public static final String UNITOP_PRODUCER_NAMESERVER             = "unitop.producer.nameserver";
    public static final String UNITOP_PRODUCER_INSTANCENAME           = "unitop.producer.instancename";
    public static final String UNITOP_PRODUCER_TOPICQUEUENUMS         = "unitop.producer.topicqueuenums";
    public static final String UNITOP_PRODUCER_TIMEOUT                = "unitop.producer.sendmsgtimeoutmillis";
    public static final String UNITOP_PRODUCER_GROUPNAME              = "unitop.producer.groupname";
}
