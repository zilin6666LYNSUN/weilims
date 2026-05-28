package com.wei.lims.reagent.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 试剂出入库记录对象 lims_reagent_stock_log
 */
public class LimsReagentStockLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long stockLogId;

    private Long reagentId;

    @Excel(name = "操作类型: IN-入库/OUT-出库/RETURN-归还")
    private String operationType;

    @Excel(name = "操作数量")
    private BigDecimal quantity;

    @Excel(name = "剩余库存")
    private BigDecimal remainQty;

    @Excel(name = "关联单号")
    private String relatedOrderNo;

    @Excel(name = "用途")
    private String purpose;

    private Long operatorId;

    @Excel(name = "操作人")
    private String operatorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;

    public Long getStockLogId() { return stockLogId; }
    public void setStockLogId(Long stockLogId) { this.stockLogId = stockLogId; }
    public Long getReagentId() { return reagentId; }
    public void setReagentId(Long reagentId) { this.reagentId = reagentId; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getRemainQty() { return remainQty; }
    public void setRemainQty(BigDecimal remainQty) { this.remainQty = remainQty; }
    public String getRelatedOrderNo() { return relatedOrderNo; }
    public void setRelatedOrderNo(String relatedOrderNo) { this.relatedOrderNo = relatedOrderNo; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public Date getOperationTime() { return operationTime; }
    public void setOperationTime(Date operationTime) { this.operationTime = operationTime; }
}
