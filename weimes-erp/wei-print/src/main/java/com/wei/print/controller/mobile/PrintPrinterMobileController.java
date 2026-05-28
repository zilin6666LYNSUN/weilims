package com.wei.print.controller.mobile;

import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.print.domain.PrintPrinterConfig;
import com.wei.print.service.IPrintPrinterConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author skyyan
 */
@Api("标签打印机查询接口")
@RestController
@RequestMapping("/mobile/print/printerconfig")
public class PrintPrinterMobileController extends BaseController {

    @Autowired
    private IPrintPrinterConfigService printPrinterConfigService;

    @ApiOperation("查询打印机清单")
    @GetMapping("/list")
    public AjaxResult getPrinterList(PrintPrinterConfig param){
        List<PrintPrinterConfig> list = printPrinterConfigService.selectPrintPrinterConfigList(param);
        return AjaxResult.success(list);
    }




}
