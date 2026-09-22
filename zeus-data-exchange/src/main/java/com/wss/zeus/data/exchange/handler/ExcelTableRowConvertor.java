package com.wss.zeus.data.exchange.handler;

import com.wss.zeus.core.common.PageData;

public interface ExcelTableRowConvertor<S, R> {

    /**
     * 数据防腐
     *
     * @param pageData
     * @return
     */
    PageData<R> convert(PageData<S> pageData);

}
