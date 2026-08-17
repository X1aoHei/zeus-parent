package com.wss.zeus.mq.listener;

import com.wss.zeus.mq.annotation.TransactionTopic;
import com.wss.zeus.mq.handler.TransactionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用事务消息监听器
 * <p>
 * 由框架提供，根据 topic:tag 自动分发到对应的 {@link TransactionHandler}
 * </p>
 *
 * @author wangshusheng
 */
@Slf4j
@RocketMQTransactionListener
public class ZeusTransactionListener implements RocketMQLocalTransactionListener {

    /**
     * Handler 映射，key = topic:tag
     */
    private final Map<String, TransactionHandler> handlerMap = new ConcurrentHashMap<>();

    /**
     * 注入所有 TransactionHandler 实现，根据 @TransactionTopic 注解建立映射
     */
    @Autowired
    public void setHandlers(List<TransactionHandler> handlers) {
        for (TransactionHandler handler : handlers) {
            TransactionTopic annotation = handler.getClass().getAnnotation(TransactionTopic.class);
            if (annotation != null) {
                String key = buildKey(annotation.topic(), annotation.tag());
                handlerMap.put(key, handler);
                log.info("注册事务处理器: {} -> {}", key, handler.getClass().getSimpleName());
            }
        }
    }

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        TransactionHandler handler = findHandler(message);
        if (handler == null) {
            log.error("未找到对应的事务处理器");
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        try {
            return handler.execute(message, arg);
        } catch (Exception e) {
            log.error("本地事务执行异常", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        TransactionHandler handler = findHandler(message);
        if (handler == null) {
            log.error("未找到对应的事务处理器");
            return RocketMQLocalTransactionState.UNKNOWN;
        }

        try {
            return handler.check(message);
        } catch (Exception e) {
            log.error("事务回查异常", e);
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }

    /**
     * 根据消息的 topic 和 tag 查找对应的 Handler
     */
    private TransactionHandler findHandler(Message message) {
        String destination = (String) message.getHeaders().get("rocketmq_TOPIC");
        String tags = (String) message.getHeaders().get("rocketmq_TAGS");

        // 先尝试精确匹配 topic:tag
        String key = buildKey(destination, tags);
        TransactionHandler handler = handlerMap.get(key);
        if (handler != null) {
            return handler;
        }

        // 再尝试 topic:* 通配符匹配
        key = buildKey(destination, "*");
        return handlerMap.get(key);
    }

    /**
     * 构建 key
     */
    private String buildKey(String topic, String tag) {
        return topic + ":" + tag;
    }
}
