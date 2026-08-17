package com.wss.zeus.mq.config;

import com.wss.zeus.mq.producer.ZeusMqProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * MQ 自动配置
 *
 * @author wangshusheng
 */
@AutoConfiguration
@ConditionalOnBean(RocketMQTemplate.class)
public class ZeusMqAutoConfiguration {

    @Bean
    public ZeusMqProducer zeusMqProducer(RocketMQTemplate rocketMQTemplate) {
        return new ZeusMqProducer(rocketMQTemplate);
    }
}
