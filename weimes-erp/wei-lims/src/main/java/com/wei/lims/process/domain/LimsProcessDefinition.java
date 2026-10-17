package com.wei.lims.process.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 流程定义对象 lims_process_definition
 * P1 可配置审批工作流：bpmn_xml 字段存储流程 JSON 定义
 * （节点数组 + 审批人/会签 + 条件分支），由通用审批引擎解析执行。
 */
public class LimsProcessDefinition extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 流程定义ID */
    private Long procDefId;

    /** 流程KEY(唯一) */
    @Excel(name = "流程KEY")
    private String procKey;

    /** 流程名称 */
    @Excel(name = "流程名称")
    private String procName;

    /** 业务类型: TEST_ORDER/SAMPLE/INSTRUMENT/REPORT/DEVIATION/CHANGE/COMPLAINT */
    @Excel(name = "业务类型")
    private String procType;

    /** 流程定义JSON(BPMN2.0兼容文本) */
    private String bpmnXml;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    /** 状态: DRAFT/ACTIVE/INACTIVE */
    @Excel(name = "状态")
    private String status;

    /** 生效日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDate;

    public Long getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(Long procDefId) {
        this.procDefId = procDefId;
    }

    public String getProcKey() {
        return procKey;
    }

    public void setProcKey(String procKey) {
        this.procKey = procKey;
    }

    public String getProcName() {
        return procName;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }

    public String getProcType() {
        return procType;
    }

    public void setProcType(String procType) {
        this.procType = procType;
    }

    public String getBpmnXml() {
        return bpmnXml;
    }

    public void setBpmnXml(String bpmnXml) {
        this.bpmnXml = bpmnXml;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
