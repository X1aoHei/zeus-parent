package com.wss.zeus.mq.annotation;

import java.lang.annotation.*;

/**
 * 事务消息主题注解
 * <p>
 * 标注在 {@link com.wss.zeus.mq.handler.TransactionHandler} 实现类上，
 * 用于指定该 Handler 处理的 topic 和 tag
 * </p>
 *
 * @author wangshusheng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TransactionTopic {

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
}
