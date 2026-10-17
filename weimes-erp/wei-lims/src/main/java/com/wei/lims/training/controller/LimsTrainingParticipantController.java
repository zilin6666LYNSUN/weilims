package com.wei.lims.training.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.training.domain.LimsTrainingParticipant;
import com.wei.lims.training.service.ILimsTrainingParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/training/participant")
public class LimsTrainingParticipantController extends BaseController {

    @Autowired
    private ILimsTrainingParticipantService limstrainingparticipantService;

    @PreAuthorize("@ss.hasPermi('lims:training:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsTrainingParticipant limstrainingparticipant) {
        startPage();
        List<LimsTrainingParticipant> list = limstrainingparticipantService.selectLimsTrainingParticipantList(limstrainingparticipant);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:training:record:list')")
    @GetMapping("/list/{trainingId}")
    public AjaxResult listByTrainingId(@PathVariable Long trainingId) {
        return AjaxResult.success(limstrainingparticipantService.selectByTrainingId(trainingId));
    }

    @PreAuthorize("@ss.hasPermi('lims:training:record:query')")
    @GetMapping("/{participantId}")
    public AjaxResult getInfo(@PathVariable Long participantId) {
        return AjaxResult.success(limstrainingparticipantService.selectLimsTrainingParticipantByParticipantId(participantId));
    }

    @PreAuthorize("@ss.hasPermi('lims:training:record:add')")
    @Log(title = "培训参与人员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsTrainingParticipant limstrainingparticipant) {
        return toAjax(limstrainingparticipantService.insertLimsTrainingParticipant(limstrainingparticipant));
    }

    @PreAuthorize("@ss.hasPermi('lims:training:record:edit')")
    @Log(title = "培训参与人员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsTrainingParticipant limstrainingparticipant) {
        return toAjax(limstrainingparticipantService.updateLimsTrainingParticipant(limstrainingparticipant));
    }

    @PreAuthorize("@ss.hasPermi('lims:training:record:remove')")
    @Log(title = "培训参与人员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{participantIds}")
    public AjaxResult remove(@PathVariable Long[] participantIds) {
        return toAjax(limstrainingparticipantService.deleteLimsTrainingParticipantByParticipantId(participantIds));
    }
}