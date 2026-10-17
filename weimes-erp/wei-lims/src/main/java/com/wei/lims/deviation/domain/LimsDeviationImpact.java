package com.wei.lims.deviation.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 偏差影响评估对象 lims_deviation_impact
 */
public class LimsDeviationImpact {
    private static final long serialVersionUID = 1L;

    private Long impactId;
    private Long deviationId;
    private String impactArea;
    private String impactDescription;
    private String impactLevel;
    private Long assessedBy;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date assessedDate;

    public Long getImpactId() { return impactId; }
    public void setImpactId(Long impactId) { this.impactId = impactId; }
    public Long getDeviationId() { return deviationId; }
    public void setDeviationId(Long deviationId) { this.deviationId = deviationId; }
    public String getImpactArea() { return impactArea; }
    public void setImpactArea(String impactArea) { this.impactArea = impactArea; }
    public String getImpactDescription() { return impactDescription; }
    public void setImpactDescription(String impactDescription) { this.impactDescription = impactDescription; }
    public String getImpactLevel() { return impactLevel; }
    public void setImpactLevel(String impactLevel) { this.impactLevel = impactLevel; }
    public Long getAssessedBy() { return assessedBy; }
    public void setAssessedBy(Long assessedBy) { this.assessedBy = assessedBy; }
    public Date getAssessedDate() { return assessedDate; }
    public void setAssessedDate(Date assessedDate) { this.assessedDate = assessedDate; }
}
