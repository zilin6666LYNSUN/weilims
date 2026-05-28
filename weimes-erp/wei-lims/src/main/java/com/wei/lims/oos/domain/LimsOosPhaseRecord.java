package com.wei.lims.oos.domain;

import com.wei.common.core.domain.BaseEntity;

/**
 * OOS调查阶段记录对象 lims_oos_phase_record
 */
public class LimsOosPhaseRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long phaseRecordId;

    private Long investigationId;

    private String phase;

    private String hypothesis;

    private String experimentMethod;

    private String experimentResult;

    private String conclusion;

    private Long operatorId;

    private String operatorName;

    public Long getPhaseRecordId() { return phaseRecordId; }
    public void setPhaseRecordId(Long phaseRecordId) { this.phaseRecordId = phaseRecordId; }
    public Long getInvestigationId() { return investigationId; }
    public void setInvestigationId(Long investigationId) { this.investigationId = investigationId; }
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    public String getHypothesis() { return hypothesis; }
    public void setHypothesis(String hypothesis) { this.hypothesis = hypothesis; }
    public String getExperimentMethod() { return experimentMethod; }
    public void setExperimentMethod(String experimentMethod) { this.experimentMethod = experimentMethod; }
    public String getExperimentResult() { return experimentResult; }
    public void setExperimentResult(String experimentResult) { this.experimentResult = experimentResult; }
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
}
