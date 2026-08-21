package com.wss.zeus.tokenizer.config;

import com.wss.zeus.tokenizer.core.HanLPTokenizer;
import com.wss.zeus.tokenizer.core.Tokenizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 分词器自动配置
 *
 * @author wangshusheng
 */
@AutoConfiguration
public class TokenizerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Tokenizer tokenizer() {
        return new HanLPTokenizer();
    }
}
