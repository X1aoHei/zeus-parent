package com.wss.zeus.data.exchange.business.example.api.feign;

import com.wss.zeus.core.annotation.ExcelFeign;
import com.wss.zeus.core.common.PageData;
import com.wss.zeus.core.common.Result;
import com.wss.zeus.data.exchange.business.example.api.constant.ExcelConstant;
import com.wss.zeus.data.exchange.business.example.api.req.TestExcelTableDataReq;
import com.wss.zeus.data.exchange.business.example.api.resp.TestExcelTableData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = ExcelConstant.SERVICE_NAME)
public interface ExportFeign {

    @ExcelFeign(name = ExcelConstant.BIZ_NAME, template = TestExcelTableData.class)
    @PostMapping(value = ExcelConstant.PAGE_LIST_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
    Result<PageData<TestExcelTableData>> pageList(@RequestBody TestExcelTableDataReq req);

}
