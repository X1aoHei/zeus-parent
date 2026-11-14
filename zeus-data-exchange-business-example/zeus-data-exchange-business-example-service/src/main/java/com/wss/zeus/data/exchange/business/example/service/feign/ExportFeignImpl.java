package com.wss.zeus.data.exchange.business.example.service.feign;

import com.wss.zeus.core.common.PageData;
import com.wss.zeus.core.common.Result;
import com.wss.zeus.data.exchange.business.example.api.feign.ExportFeign;
import com.wss.zeus.data.exchange.business.example.api.req.TestExcelTableDataReq;
import com.wss.zeus.data.exchange.business.example.api.resp.TestExcelTableData;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: wangshusheng
 * @Date: 2026-09-22 17:45
 */
@RestController
public class ExportFeignImpl implements ExportFeign {
    @Override
    public Result<PageData<TestExcelTableData>> pageList(TestExcelTableDataReq req) {
        TestExcelTableData testExcelTableData = new TestExcelTableData();
        testExcelTableData.setOrderNo("orderNo");
        testExcelTableData.setUserNo("userNo");

        PageData<TestExcelTableData> pageData = new PageData<>();
        pageData.setList(List.of(testExcelTableData));
        return Result.success(pageData);
    }
}
