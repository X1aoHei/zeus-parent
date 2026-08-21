package com.wss.zeus.tokenizer.core;

import java.util.List;

/**
 * 分词器接口
 *
 * @author wangshusheng
 */
public interface Tokenizer {

    /**
     * 分词（索引模式）
     * <p>
     * 适用于搜索场景，分词更细粒度
     * </p>
     *
     * @param text 待分词文本
     * @return 词语列表
     */
    List<String> segment(String text);

    /**
     * 分词（索引模式，带词性标注）
     *
     * @param text 待分词文本
     * @return 词语列表（格式：词语/词性）
     */
    List<String> segmentWithNature(String text);
}
