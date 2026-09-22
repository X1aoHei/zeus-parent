package com.wss.zeus.tokenizer.core;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.hankcs.hanlp.tokenizer.TraditionalChineseTokenizer;
import org.springframework.core.NamedInheritableThreadLocal;

import java.util.List;

class HanLPTokenizerTest {

    public static void main(String[] args) {
        //西湖文化广场
        //西湖区图书馆
        //西H区
        //东区三楼H3011
        //西湖 xh
        //西湖西湖文化广场
        //北京市北京路
        //西糊


        List<Term> termList = IndexTokenizer.segment("浙江大学");
        List<Term> termList1 = StandardTokenizer.segment("西湖畔大王1号店");
        List<Term> termList2 = TraditionalChineseTokenizer.segment("西湖畔大王1号店");
        System.out.println(123);

        String pinyin = HanLP.convertToPinyinString("A", "  ", false);
        System.out.println(pinyin);


        String pinyinChar = HanLP.convertToPinyinFirstCharString("A", "", false);
        System.out.println(pinyin);
    }

}