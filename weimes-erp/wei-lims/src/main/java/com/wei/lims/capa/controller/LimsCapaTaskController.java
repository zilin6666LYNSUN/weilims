package com.wei.lims.capa.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.capa.domain.LimsCapaTask;
import com.wei.lims.capa.service.ILimsCapaTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/quality/capa/task")
public class LimsCapaTaskController extends BaseController {

    @Autowired
    private ILimsCapaTaskService limsCapaTaskService;

    @PreAuthorize("@ss.hasPermi('lims:capa:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsCapaTask task) {
        startPage();
        List<LimsCapaTask> list = limsCapaTaskService.selectLimsCapaTaskList(task);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:query')")
    @GetMapping("/list/{capaId}")
    public AjaxResult listByCapaId(@PathVariable Long capaId) {
        return AjaxResult.success(limsCapaTaskService.selectByCapaId(capaId));
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:query')")
    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable Long taskId) {
        return AjaxResult.success(limsCapaTaskService.selectByTaskId(taskId));
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:add')")
    @Log(title = "CAPA任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsCapaTask task) {
        return toAjax(limsCapaTaskService.insertLimsCapaTask(task));
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:edit')")
    @Log(title = "CAPA任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsCapaTask task) {
        return toAjax(limsCapaTaskService.updateLimsCapaTask(task));
    }

    @PreAuthorize("@ss.hasPermi('lims:capa:remove')")
    @Log(title = "CAPA任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds) {
        return toAjax(limsCapaTaskService.deleteLimsCapaTaskByIds(taskIds));
    }

    /** 完成任务 */
    @PreAuthorize("@ss.hasPermi('lims:capa:edit')")
    @Log(title = "CAPA任务", businessType = BusinessType.UPDATE)
    @PutMapping("/complete/{taskId}")
    public AjaxResult complete(@PathVariable Long taskId) {
        return toAjax(limsCapaTaskService.completeTask(taskId));
    }
}
