package com.wss.zeus.mq.annotation;

import com.wss.zeus.mq.handler.MessageHandler;

import java.lang.annotation.*;

/**
 * Zeus MQ 消费者注解
 * <p>
 * 标注在 {@link MessageHandler} 实现类上，框架自动注册 RocketMQListener
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>
 * &#64;Component
 * &#64;ZeusMessageListener(topic = "order", tag = "create", consumerGroup = "order-group")
 * public class OrderCreateConsumer implements MessageHandler&lt;String&gt; {
 *     &#64;Override
 *     public void onMessage(String message) {
 *         // 消费逻辑
 *     }
 * }
 * </pre>
 *
 * @author wangshusheng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZeusMessageListener {

    /**
     * 主题
     *
     * @return topic
     */
    String topic();

    /**
     * 标签
     *
     * @return tag
     */
    String tag() default "*";

    /**
     * 消费者组
     *
     * @return consumerGroup
     */
    String consumerGroup();
}
