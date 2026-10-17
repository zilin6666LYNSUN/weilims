package com.wei.lims.entrust.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 委托单模板对象 lims_entrust_template
 */
public class LimsEntrustTemplate {
    private static final long serialVersionUID = 1L;

    private Long templateId;

    private String templateCode;

    private String templateName;

    private String templateFields;

    private String defaultRemark;

    private String status;

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getTemplateFields() { return templateFields; }
    public void setTemplateFields(String templateFields) { this.templateFields = templateFields; }

    public String getDefaultRemark() { return defaultRemark; }
    public void setDefaultRemark(String defaultRemark) { this.defaultRemark = defaultRemark; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}