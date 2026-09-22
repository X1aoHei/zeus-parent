package com.wss.zeus.mq.config;

import com.wss.zeus.mq.listener.ZeusTransactionListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Zeus MQ 自动配置
 * <p>
 * 消费端使用 RocketMQ 官方 {@code @RocketMQMessageListener}。
 * 这里只注册事务消息监听器。
 * </p>
 *
 * @author wangshusheng
 */
@AutoConfiguration
@Import(ZeusTransactionListener.class)
public class ZeusMqAutoConfiguration {
}
