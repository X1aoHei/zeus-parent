package com.wss.zeus.tokenizer.core;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;

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
        List<Term> termList = IndexTokenizer.segment("西糊");
        System.out.println(123);

        String pinyin = HanLP.convertToPinyinString("西糊", "", false);
        System.out.println(pinyin);

    }

}