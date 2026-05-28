package com.wei.lims.test.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.constant.UserConstants;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.common.utils.poi.ExcelUtil;
import com.wei.lims.test.domain.LimsTestOrder;
import com.wei.lims.test.domain.LimsTestResult;
import com.wei.lims.test.service.ILimsTestOrderService;
import com.wei.lims.test.service.ILimsTestResultService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 检验订单Controller
 */
@RestController
@RequestMapping("/lims/test/order")
public class LimsTestOrderController extends BaseController {

    @Autowired
    private ILimsTestOrderService limsTestOrderService;

    @Autowired
    private ILimsTestResultService limsTestResultService;

    @PreAuthorize("@ss.hasPermi('lims:test:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsTestOrder limsTestOrder) {
        startPage();
        List<LimsTestOrder> list = limsTestOrderService.selectLimsTestOrderList(limsTestOrder);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:test:order:export')")
    @Log(title = "检验订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LimsTestOrder limsTestOrder) {
        List<LimsTestOrder> list = limsTestOrderService.selectLimsTestOrderList(limsTestOrder);
        ExcelUtil<LimsTestOrder> util = new ExcelUtil<LimsTestOrder>(LimsTestOrder.class);
        util.exportExcel(response, list, "检验订单数据");
    }

    @PreAuthorize("@ss.hasPermi('lims:test:order:query')")
    @GetMapping(value = "/{testOrderId}")
    public AjaxResult getInfo(@PathVariable("testOrderId") Long testOrderId) {
        return AjaxResult.success(limsTestOrderService.selectLimsTestOrderByTestOrderId(testOrderId));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:order:add')")
    @Log(title = "检验订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsTestOrder limsTestOrder) {
        if (UserConstants.NOT_UNIQUE.equals(limsTestOrderService.checkTestOrderCodeUnique(limsTestOrder))) {
            return AjaxResult.error("检验单编号已存在！");
        }
        return toAjax(limsTestOrderService.insertLimsTestOrder(limsTestOrder));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:order:edit')")
    @Log(title = "检验订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsTestOrder limsTestOrder) {
        if (UserConstants.NOT_UNIQUE.equals(limsTestOrderService.checkTestOrderCodeUnique(limsTestOrder))) {
            return AjaxResult.error("检验单编号已存在！");
        }
        return toAjax(limsTestOrderService.updateLimsTestOrder(limsTestOrder));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:order:remove')")
    @Log(title = "检验订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{testOrderIds}")
    public AjaxResult remove(@PathVariable Long[] testOrderIds) {
        return toAjax(limsTestOrderService.deleteLimsTestOrderByTestOrderIds(testOrderIds));
    }

    /**
     * 提交检验订单
     */
    @PreAuthorize("@ss.hasPermi('lims:test:order:submit')")
    @Log(title = "提交检验订单", businessType = BusinessType.UPDATE)
    @PutMapping("/submit/{testOrderId}")
    public AjaxResult submit(@PathVariable Long testOrderId) {
        return toAjax(limsTestOrderService.submitTestOrder(testOrderId));
    }

    /**
     * 检验结果复核
     */
    @PreAuthorize("@ss.hasPermi('lims:test:order:review')")
    @Log(title = "检验复核", businessType = BusinessType.UPDATE)
    @PutMapping("/review/{testOrderId}")
    public AjaxResult review(@PathVariable Long testOrderId,
                              @RequestParam String reviewResult,
                              @RequestParam(required = false) String comment) {
        return toAjax(limsTestOrderService.reviewTestOrder(testOrderId, reviewResult, comment));
    }

    /**
     * 检验批准
     */
    @PreAuthorize("@ss.hasPermi('lims:test:order:approve')")
    @Log(title = "检验批准", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{testOrderId}")
    public AjaxResult approve(@PathVariable Long testOrderId,
                              @RequestParam String approveResult,
                              @RequestParam(required = false) String comment) {
        return toAjax(limsTestOrderService.approveTestOrder(testOrderId, approveResult, comment));
    }

    /**
     * 查询检验结果列表
     */
    @PreAuthorize("@ss.hasPermi('lims:test:result:list')")
    @GetMapping("/result/{testOrderId}")
    public AjaxResult resultList(@PathVariable Long testOrderId) {
        return AjaxResult.success(limsTestResultService.selectLimsTestResultByTestOrderId(testOrderId));
    }
}
