package com.wei.lims.reagent.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;

/**
 * 标准品使用记录对象 lims_standard_usage
 */
public class LimsStandardUsage {
    private static final long serialVersionUID = 1L;

    private Long usageId;

    private Long standardId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date usedDate;

    private Long usedBy;

    private Long testOrderId;

    @Excel(name = "使用量")
    private BigDecimal quantityUsed;

    @Excel(name = "使用后剩余")
    private BigDecimal remainingAfter;

    public Long getUsageId() { return usageId; }
    public void setUsageId(Long usageId) { this.usageId = usageId; }
    public Long getStandardId() { return standardId; }
    public void setStandardId(Long standardId) { this.standardId = standardId; }
    public Date getUsedDate() { return usedDate; }
    public void setUsedDate(Date usedDate) { this.usedDate = usedDate; }
    public Long getUsedBy() { return usedBy; }
    public void setUsedBy(Long usedBy) { this.usedBy = usedBy; }
    public Long getTestOrderId() { return testOrderId; }
    public void setTestOrderId(Long testOrderId) { this.testOrderId = testOrderId; }
    public BigDecimal getQuantityUsed() { return quantityUsed; }
    public void setQuantityUsed(BigDecimal quantityUsed) { this.quantityUsed = quantityUsed; }
    public BigDecimal getRemainingAfter() { return remainingAfter; }
    public void setRemainingAfter(BigDecimal remainingAfter) { this.remainingAfter = remainingAfter; }
}
