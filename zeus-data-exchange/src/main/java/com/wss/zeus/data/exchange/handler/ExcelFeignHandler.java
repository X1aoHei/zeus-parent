package com.wss.zeus.data.exchange.handler;

import com.alibaba.fastjson2.JSONObject;

public interface ExcelFeignHandler {

    void execute(String bizName, JSONObject param);

}
