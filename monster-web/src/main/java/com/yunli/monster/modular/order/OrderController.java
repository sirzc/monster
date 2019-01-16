package com.yunli.monster.modular.order;

import com.yunli.monster.core.config.MqConfig;
import com.yunli.monster.core.mq.core.OrderCustomer;
import com.yunli.monster.core.mq.core.OrderProducer;
import com.yunli.monster.core.util.DateUtil;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 单号类
 *
 * @author zhouchao
 * @create 2019-01-02 11:29
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    MqConfig mqConfig;

    @RequestMapping("")
    public String test() {
        return "test";
    }

    @RequestMapping("/generate/{num}")
    public List<SendResult> generateOrderNumber(@PathVariable int num) {
        OrderProducer producer = mqConfig.orderProducer();
        List<SendResult> results = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            results.add(producer.send("TOPIC-" + DateUtil.getDay(), "TAGS", DateUtil.getTime() + "=" + i));
        }
        return results;
    }

    @RequestMapping("/get/{num}")
    public List<String> getOrderNum(@PathVariable int num) {
        List<String> list = new ArrayList<>();
        OrderCustomer customer = mqConfig.orderCustomer();
        List<MessageExt> mes = customer.listMessage("TOPIC-" + DateUtil.getDay(), "TAGS", num);
        if (null != mes) {
            for (MessageExt me : mes) {
                list.add(new String(me.getBody(), StandardCharsets.UTF_8));
            }
        }
        return list;
    }
}
