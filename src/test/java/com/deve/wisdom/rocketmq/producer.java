//-*- coding =utf-8 -*-
//@Time : 2023/8/20
//@Author: 邓闽川
//@File  producer.java
//@software:IntelliJ IDEA
package com.deve.wisdom.rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class producer {
    public static void main(String[] args) throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        DefaultMQProducer producer=new DefaultMQProducer("testProducer");
        producer.setNamesrvAddr("124.222.117.145:9876");
        producer.setSendMsgTimeout(60000);
        producer.start();
        Message m = new Message("test", "first message".getBytes());
        producer.send(m);
        producer.shutdown();
    }
}
