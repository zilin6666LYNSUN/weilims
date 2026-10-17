package com.wei.lims.change.domain;

/**
 * 变更影响评估对象 lims_change_impact
 */
public class LimsChangeImpact {
    private static final long serialVersionUID = 1L;

    private Long impactId;
    private Long changeId;
    private String impactArea;
    private String impactDetail;
    private String riskLevel;
    private String mitigationPlan;

    public Long getImpactId() { return impactId; }
    public void setImpactId(Long impactId) { this.impactId = impactId; }
    public Long getChangeId() { return changeId; }
    public void setChangeId(Long changeId) { this.changeId = changeId; }
    public String getImpactArea() { return impactArea; }
    public void setImpactArea(String impactArea) { this.impactArea = impactArea; }
    public String getImpactDetail() { return impactDetail; }
    public void setImpactDetail(String impactDetail) { this.impactDetail = impactDetail; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getMitigationPlan() { return mitigationPlan; }
    public void setMitigationPlan(String mitigationPlan) { this.mitigationPlan = mitigationPlan; }
}
