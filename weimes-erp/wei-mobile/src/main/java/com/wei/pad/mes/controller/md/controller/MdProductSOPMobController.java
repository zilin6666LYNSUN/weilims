package com.wei.pad.mes.controller.md.controller;

import com.wei.common.constant.UserConstants;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.mes.md.domain.MdProductSop;
import com.wei.mes.md.service.IMdProductSopService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(UserConstants.PAD_PATH + "/md/sop")
public class MdProductSOPMobController extends BaseController {
    @Autowired
    private IMdProductSopService mdProductSopService;

    /**
     * 查询产品SOP列表
     */
    @ApiOperation("查询产品SOP信息")
    @GetMapping("/list")
    public AjaxResult list(MdProductSop mdProdutSop) {
        startPage();
        List<MdProductSop> list = mdProductSopService.selectMdProductSopList(mdProdutSop);
        return AjaxResult.success(list);
    }
}
