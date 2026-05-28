package com.wei.lims.report.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 报告模板对象 lims_report_template
 */
public class LimsReportTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long templateId;

    @Excel(name = "模板编号")
    private String templateCode;

    @Excel(name = "模板名称")
    private String templateName;

    @Excel(name = "模板类型")
    private String templateType;

    @Excel(name = "模板分类")
    private String templateCategory;

    private String description;

    private String versionNumber;

    private String paperSize;

    private String orientation;

    private BigDecimal marginTop;
    private BigDecimal marginBottom;
    private BigDecimal marginLeft;
    private BigDecimal marginRight;

    private String headerContent;
    private String footerContent;
    private String watermark;

    private Integer isDefault;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;

    @Excel(name = "状态")
    private String status;

    private String procInstId;

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }
    public String getTemplateCategory() { return templateCategory; }
    public void setTemplateCategory(String templateCategory) { this.templateCategory = templateCategory; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVersionNumber() { return versionNumber; }
    public void setVersionNumber(String versionNumber) { this.versionNumber = versionNumber; }
    public String getPaperSize() { return paperSize; }
    public void setPaperSize(String paperSize) { this.paperSize = paperSize; }
    public String getOrientation() { return orientation; }
    public void setOrientation(String orientation) { this.orientation = orientation; }
    public BigDecimal getMarginTop() { return marginTop; }
    public void setMarginTop(BigDecimal marginTop) { this.marginTop = marginTop; }
    public BigDecimal getMarginBottom() { return marginBottom; }
    public void setMarginBottom(BigDecimal marginBottom) { this.marginBottom = marginBottom; }
    public BigDecimal getMarginLeft() { return marginLeft; }
    public void setMarginLeft(BigDecimal marginLeft) { this.marginLeft = marginLeft; }
    public BigDecimal getMarginRight() { return marginRight; }
    public void setMarginRight(BigDecimal marginRight) { this.marginRight = marginRight; }
    public String getHeaderContent() { return headerContent; }
    public void setHeaderContent(String headerContent) { this.headerContent = headerContent; }
    public String getFooterContent() { return footerContent; }
    public void setFooterContent(String footerContent) { this.footerContent = footerContent; }
    public String getWatermark() { return watermark; }
    public void setWatermark(String watermark) { this.watermark = watermark; }
    public Integer getIsDefault() { return isDefault; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }
    public Date getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(Date effectiveDate) { this.effectiveDate = effectiveDate; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProcInstId() { return procInstId; }
    public void setProcInstId(String procInstId) { this.procInstId = procInstId; }
}
