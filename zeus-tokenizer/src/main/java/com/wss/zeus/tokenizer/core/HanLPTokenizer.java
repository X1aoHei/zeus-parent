package com.wss.zeus.tokenizer.core;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * HanLP 分词器实现（索引模式）
 * <p>
 * 使用 IndexTokenizer，适用于搜索场景，分词更细粒度
 * </p>
 *
 * @author wangshusheng
 */
public class HanLPTokenizer implements Tokenizer {

    @Override
    public List<String> segment(String text) {
        List<Term> terms = IndexTokenizer.segment(text);
        List<String> result = new ArrayList<>(terms.size());
        for (Term term : terms) {
            result.add(term.word);
        }
        return result;
    }

    @Override
    public List<String> segmentWithNature(String text) {
        List<Term> terms = IndexTokenizer.segment(text);
        List<String> result = new ArrayList<>(terms.size());
        for (Term term : terms) {
            result.add(term.word + "/" + term.nature);
        }
        return result;
    }
}
