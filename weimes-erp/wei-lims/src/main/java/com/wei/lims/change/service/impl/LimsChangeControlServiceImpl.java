package com.wei.lims.change.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.change.domain.LimsChangeControl;
import com.wei.lims.change.domain.LimsChangeImpact;
import com.wei.lims.change.mapper.LimsChangeControlMapper;
import com.wei.lims.change.mapper.LimsChangeImpactMapper;
import com.wei.lims.change.service.ILimsChangeControlService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsChangeControlServiceImpl implements ILimsChangeControlService {

    @Autowired
    private LimsChangeControlMapper limsChangeControlMapper;

    @Autowired
    private LimsChangeImpactMapper limsChangeImpactMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsChangeControl selectByChangeId(Long changeId) {
        return limsChangeControlMapper.selectByChangeId(changeId);
    }

    @Override
    public List<LimsChangeControl> selectLimsChangeControlList(LimsChangeControl changeControl) {
        return limsChangeControlMapper.selectLimsChangeControlList(changeControl);
    }

    @Override
    @Transactional
    public int insertLimsChangeControl(LimsChangeControl changeControl) {
        changeControl.setCreateTime(DateUtils.getNowDate());
        int rows = limsChangeControlMapper.insertLimsChangeControl(changeControl);
        limsAuditTrailService.recordFieldChanges("lims_change_control", changeControl.getChangeId(),
                "INSERT", null, changeControl, "新建变更控制");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsChangeControl(LimsChangeControl changeControl) {
        LimsChangeControl oldChangeControl = limsChangeControlMapper.selectByChangeId(changeControl.getChangeId());
        changeControl.setUpdateTime(DateUtils.getNowDate());
        int rows = limsChangeControlMapper.updateLimsChangeControl(changeControl);
        limsAuditTrailService.recordFieldChanges("lims_change_control", changeControl.getChangeId(),
                "UPDATE", oldChangeControl, changeControl, "更新变更控制");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsChangeControlByIds(Long[] changeIds) {
        for (Long changeId : changeIds) {
            limsAuditTrailService.recordFieldChanges("lims_change_control", changeId,
                    "DELETE", null, null, "删除变更控制");
        }
        return limsChangeControlMapper.deleteLimsChangeControlByIds(changeIds);
    }

    @Override
    @Transactional
    public int submitChange(Long changeId) {
        LimsChangeControl oldC = limsChangeControlMapper.selectByChangeId(changeId);
        LimsChangeControl newC = limsChangeControlMapper.selectByChangeId(changeId);
        newC.setStatus("SUBMITTED");
        int rows = limsChangeControlMapper.updateLimsChangeControl(newC);
        limsAuditTrailService.recordFieldChanges("lims_change_control", changeId,
                "UPDATE", oldC, newC, "提交变更");
        return rows;
    }

    @Override
    @Transactional
    public int approveChange(Long changeId) {
        LimsChangeControl oldC = limsChangeControlMapper.selectByChangeId(changeId);
        LimsChangeControl newC = limsChangeControlMapper.selectByChangeId(changeId);
        newC.setStatus("APPROVED");
        int rows = limsChangeControlMapper.updateLimsChangeControl(newC);
        limsAuditTrailService.recordFieldChanges("lims_change_control", changeId,
                "UPDATE", oldC, newC, "审批变更");
        // 自动触发变更影响评估任务（待质量人员完善评估结论）
        LimsChangeImpact impact = new LimsChangeImpact();
        impact.setChangeId(changeId);
        impact.setImpactArea("产品");
        impact.setRiskLevel("中");
        impact.setImpactDetail("变更审批通过，需评估对受影响批次/产品/客户的影响");
        limsChangeImpactMapper.insertLimsChangeImpact(impact);
        limsAuditTrailService.recordFieldChanges("lims_change_impact", impact.getImpactId(),
                "INSERT", null, impact, "审批变更自动触发影响评估");
        return rows;
    }

    @Override
    @Transactional
    public int implementChange(Long changeId) {
        LimsChangeControl oldC = limsChangeControlMapper.selectByChangeId(changeId);
        LimsChangeControl newC = limsChangeControlMapper.selectByChangeId(changeId);
        newC.setStatus("IMPLEMENTING");
        newC.setEffectiveDate(DateUtils.getNowDate());
        int rows = limsChangeControlMapper.updateLimsChangeControl(newC);
        limsAuditTrailService.recordFieldChanges("lims_change_control", changeId,
                "UPDATE", oldC, newC, "实施变更");
        return rows;
    }

    @Override
    @Transactional
    public int completeChange(Long changeId) {
        LimsChangeControl oldC = limsChangeControlMapper.selectByChangeId(changeId);
        LimsChangeControl newC = limsChangeControlMapper.selectByChangeId(changeId);
        newC.setStatus("COMPLETED");
        newC.setEffectiveDate(DateUtils.getNowDate());
        int rows = limsChangeControlMapper.updateLimsChangeControl(newC);
        limsAuditTrailService.recordFieldChanges("lims_change_control", changeId,
                "UPDATE", oldC, newC, "完成变更");
        return rows;
    }
}
