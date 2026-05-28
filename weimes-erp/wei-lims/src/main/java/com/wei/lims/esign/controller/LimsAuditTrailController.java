package com.wei.lims.esign.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.esign.domain.LimsAuditTrail;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 审计追踪Controller
 * 只提供查询，不提供新增/修改/删除(由AOP自动记录)
 */
@RestController
@RequestMapping("/lims/audit-trail")
public class LimsAuditTrailController extends BaseController {

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    /**
     * 查询审计追踪列表
     */
    @PreAuthorize("@ss.hasPermi('lims:esign:audit:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsAuditTrail limsAuditTrail) {
        startPage();
        List<LimsAuditTrail> list = limsAuditTrailService.selectLimsAuditTrailList(limsAuditTrail);
        return getDataTable(list);
    }

    /**
     * 查询指定表和记录的变更历史
     */
    @PreAuthorize("@ss.hasPermi('lims:esign:audit:query')")
    @GetMapping("/history/{tableName}/{recordId}")
    public AjaxResult getHistory(@PathVariable String tableName, @PathVariable Long recordId) {
        return AjaxResult.success(limsAuditTrailService.selectByTableAndRecord(tableName, recordId));
    }
}
