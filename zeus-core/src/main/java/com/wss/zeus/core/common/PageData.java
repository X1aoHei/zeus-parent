package com.wss.zeus.core.common;

import lombok.Data;

import java.util.List;

/**
 * @author: wangshusheng
 * @Date: 2026-08-12 16:29
 */
@Data
public class PageData<T> {

    private List<T> list;

}
