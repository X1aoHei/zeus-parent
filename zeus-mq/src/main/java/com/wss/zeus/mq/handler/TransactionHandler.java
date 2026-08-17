package com.wss.zeus.mq.handler;

import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;

/**
 * 事务消息处理器接口
 * <p>
 * 引用方实现此接口，框架自动注入到 TransactionListener 中
 * </p>
 *
 * @author wangshusheng
 */
public interface TransactionHandler {

    /**
     * 执行本地事务
     *
     * @param message 消息
     * @param arg     业务参数
     * @return 事务状态
     */
    RocketMQLocalTransactionState execute(Message message, Object arg);

    /**
     * 事务回查
     *
     * @param message 消息
     * @return 事务状态
     */
    RocketMQLocalTransactionState check(Message message);
}
