package com.wss.zeus.mq.listener;

import com.wss.zeus.mq.annotation.ZeusMessageListener;
import com.wss.zeus.mq.handler.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQMessageListenerContainer;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * MQ 消息监听器注册器
 * <p>
 * 自动扫描 {@link ZeusMessageListener} 注解，动态注册 RocketMQListener
 * </p>
 *
 * @author wangshusheng
 */
@Slf4j
@Component
public class ZeusMessageListenerRegistrar implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 获取所有 MessageHandler 实现
        Map<String, MessageHandler> handlers = beanFactory.getBeansOfType(MessageHandler.class);

        for (Map.Entry<String, MessageHandler> entry : handlers.entrySet()) {
            String beanName = entry.getKey();
            MessageHandler handler = entry.getValue();

            ZeusMessageListener annotation = handler.getClass().getAnnotation(ZeusMessageListener.class);
            if (annotation == null) {
                continue;
            }

            // 动态注册 RocketMQListener
            registerListener(beanName, handler, annotation);
        }
    }

    /**
     * 动态注册 RocketMQListener
     */
    @SuppressWarnings("unchecked")
    private void registerListener(String beanName, MessageHandler handler, ZeusMessageListener annotation) {
        // 创建 RocketMQListener 包装器
        RocketMQListener<Object> listener = handler::onMessage;

        // 创建 DefaultRocketMQListenerContainer
        DefaultRocketMQListenerContainer container = new DefaultRocketMQListenerContainer();
        container.setRocketMQMessageListener(annotation);
        container.setRocketMQListener(listener);
        container.setNameServer(beanFactory.resolveEmbeddedValue("${rocketmq.name-server}"));
        container.setTopic(annotation.topic());
        container.setConsumerGroup(annotation.consumerGroup());

        // 设置 tag
        String tag = annotation.tag();
        if (!"*".equals(tag)) {
            container.setSelectorExpression(tag);
        }

        // 注册到容器
        String containerBeanName = "zeus_" + beanName + "_container";
        beanFactory.registerSingleton(containerBeanName, container);

        log.info("注册 MQ 消费者: topic={}, tag={}, consumerGroup={}, handler={}",
                annotation.topic(), annotation.tag(), annotation.consumerGroup(), handler.getClass().getSimpleName());
    }
}
