package com.wei.lims.stability.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.stability.domain.LimsStabilityProtocol;
import com.wei.lims.stability.domain.LimsStabilityTest;
import com.wei.lims.stability.mapper.LimsStabilityTestMapper;
import com.wei.lims.stability.service.ILimsStabilityProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 稳定性考察Controller
 */
@RestController
@RequestMapping("/lims/stability")
public class LimsStabilityController extends BaseController {

    @Autowired
    private ILimsStabilityProtocolService limsStabilityProtocolService;

    @Autowired
    private LimsStabilityTestMapper limsStabilityTestMapper;

    @PreAuthorize("@ss.hasPermi('lims:stability:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsStabilityProtocol protocol) {
        startPage();
        List<LimsStabilityProtocol> list = limsStabilityProtocolService.selectLimsStabilityProtocolList(protocol);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:stability:query')")
    @GetMapping("/{protocolId}")
    public AjaxResult getInfo(@PathVariable Long protocolId) {
        return AjaxResult.success(limsStabilityProtocolService.selectByProtocolId(protocolId));
    }

    @PreAuthorize("@ss.hasPermi('lims:stability:add')")
    @Log(title = "稳定性考察", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsStabilityProtocol protocol) {
        return toAjax(limsStabilityProtocolService.insertLimsStabilityProtocol(protocol));
    }

    @PreAuthorize("@ss.hasPermi('lims:stability:edit')")
    @Log(title = "稳定性考察", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsStabilityProtocol protocol) {
        return toAjax(limsStabilityProtocolService.updateLimsStabilityProtocol(protocol));
    }

    @PreAuthorize("@ss.hasPermi('lims:stability:remove')")
    @Log(title = "稳定性考察", businessType = BusinessType.DELETE)
    @DeleteMapping("/{protocolIds}")
    public AjaxResult remove(@PathVariable Long[] protocolIds) {
        return toAjax(limsStabilityProtocolService.deleteLimsStabilityProtocolByProtocolIds(protocolIds));
    }

    /** 考察点列表 */
    @PreAuthorize("@ss.hasPermi('lims:stability:query')")
    @GetMapping("/test/list/{protocolId}")
    public AjaxResult testList(@PathVariable Long protocolId) {
        return AjaxResult.success(limsStabilityTestMapper.selectByProtocolId(protocolId));
    }

    @PreAuthorize("@ss.hasPermi('lims:stability:edit')")
    @Log(title = "稳定性考察-录入结果", businessType = BusinessType.UPDATE)
    @PutMapping("/test")
    public AjaxResult updateTest(@RequestBody LimsStabilityTest test) {
        return toAjax(limsStabilityTestMapper.updateLimsStabilityTest(test));
    }
}
