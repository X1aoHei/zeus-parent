package com.wss.zeus.mq.producer;

import com.wss.zeus.mq.model.MqMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 通用 MQ 生产者
 *
 * @author wangshusheng
 */
@Slf4j
@RequiredArgsConstructor
public class ZeusMqProducer {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 发送消息
     *
     * @param topic   主题
     * @param tag     标签
     * @param message 消息体
     * @param <T>     消息体类型
     */
    public <T> void send(String topic, String tag, MqMessage<T> message) {
        String destination = topic + ":" + tag;
        Message<MqMessage<T>> msg = MessageBuilder.withPayload(message)
                .setHeader("KEYS", message.getMessageId())
                .build();
        log.info("发送MQ消息, destination={}, messageId={}", destination, message.getMessageId());
        rocketMQTemplate.send(destination, msg);
    }

    /**
     * 同步发送消息
     *
     * @param topic   主题
     * @param tag     标签
     * @param message 消息体
     * @param <T>     消息体类型
     */
    public <T> void syncSend(String topic, String tag, MqMessage<T> message) {
        String destination = topic + ":" + tag;
        Message<MqMessage<T>> msg = MessageBuilder.withPayload(message)
                .setHeader("KEYS", message.getMessageId())
                .build();
        log.info("同步发送MQ消息, destination={}, messageId={}", destination, message.getMessageId());
        rocketMQTemplate.syncSend(destination, msg);
    }
}
