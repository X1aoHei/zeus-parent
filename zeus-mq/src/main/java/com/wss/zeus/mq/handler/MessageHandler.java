package com.wss.zeus.mq.handler;

/**
 * MQ 消息处理器接口
 * <p>
 * 引用方实现此接口，配合 {@link com.wss.zeus.mq.annotation.ZeusMessageListener} 使用，
 * 框架自动注册 RocketMQListener
 * </p>
 *
 * @param <T> 消息体类型
 * @author wangshusheng
 */
public interface MessageHandler<T> {

    /**
     * 消费消息
     *
     * @param message 消息体
     */
    void onMessage(T message);
}
