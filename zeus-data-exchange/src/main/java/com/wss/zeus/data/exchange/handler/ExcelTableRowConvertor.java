package com.wss.zeus.data.exchange.handler;

import com.wss.zeus.core.common.PageData;

public interface ExcelTableRowConvertor<S, R> {

    PageData<R> convert(PageData<S> pageData);

}
