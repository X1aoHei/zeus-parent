package com.wss.zeus.mq.consumer;

import com.wss.zeus.mq.model.MqMessage;

/**
 * MQ 消费者接口
 * <p>
 * 业务方实现此接口来处理消息
 * </p>
 *
 * @author wangshusheng
 */
public interface ZeusMqConsumer<T> {

    /**
     * 消费消息
     *
     * @param message 消息体
     */
    void onMessage(MqMessage<T> message);
}
