package com.wei.lims.capa.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.capa.domain.LimsCapa;
import com.wei.lims.capa.service.ILimsCapaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/capa")
public class LimsCapaController extends BaseController {

    @Autowired
    private ILimsCapaService limsCapaService;

    @PreAuthorize("@ss.hasPermi('lims:capa:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsCapa capa) {
        startPage();
        List<LimsCapa> list = limsCapaService.selectLimsCapaList(capa);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:query')")
    @GetMapping("/{capaId}")
    public AjaxResult getInfo(@PathVariable Long capaId) {
        return AjaxResult.success(limsCapaService.selectByCapaId(capaId));
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:add')")
    @Log(title = "CAPA管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsCapa capa) {
        return toAjax(limsCapaService.insertLimsCapa(capa));
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:edit')")
    @Log(title = "CAPA管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsCapa capa) {
        return toAjax(limsCapaService.updateLimsCapa(capa));
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:remove')")
    @Log(title = "CAPA管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{capaIds}")
    public AjaxResult remove(@PathVariable Long[] capaIds) {
        return toAjax(limsCapaService.deleteLimsCapaByIds(capaIds));
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:edit')")
    @PutMapping("/submit/{capaId}")
    public AjaxResult submit(@PathVariable Long capaId) {
        return toAjax(limsCapaService.submitCapa(capaId));
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:approve')")
    @PutMapping("/approve/{capaId}")
    public AjaxResult approve(@PathVariable Long capaId) {
        return toAjax(limsCapaService.approveCapa(capaId));
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:close')")
    @PutMapping("/close/{capaId}")
    public AjaxResult close(@PathVariable Long capaId) {
        return toAjax(limsCapaService.closeCapa(capaId));
    }
}
