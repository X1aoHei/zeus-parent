package com.wss.zeus.mq.model;

import lombok.Data;

import java.io.Serializable;

/**
 * MQ 消息体
 *
 * @author wangshusheng
 */
@Data
public class MqMessage<T> implements Serializable {

    /**
     * 消息唯一标识（用于幂等）
     */
    private String messageId;

    /**
     * 业务数据
     */
    private T body;

    public MqMessage() {
    }

    public MqMessage(String messageId, T body) {
        this.messageId = messageId;
        this.body = body;
    }

    public static <T> MqMessage<T> of(String messageId, T body) {
        return new MqMessage<>(messageId, body);
    }
}
