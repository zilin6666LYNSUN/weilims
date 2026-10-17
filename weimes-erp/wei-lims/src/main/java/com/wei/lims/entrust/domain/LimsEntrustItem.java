package com.wei.lims.entrust.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 委托检验项目对象 lims_entrust_item
 */
public class LimsEntrustItem {
    private static final long serialVersionUID = 1L;

    private Long itemId;

    private Long entrustId;

    private String testItemName;

    private String testMethod;

    private String standardRef;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private String status;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getEntrustId() { return entrustId; }
    public void setEntrustId(Long entrustId) { this.entrustId = entrustId; }

    public String getTestItemName() { return testItemName; }
    public void setTestItemName(String testItemName) { this.testItemName = testItemName; }

    public String getTestMethod() { return testMethod; }
    public void setTestMethod(String testMethod) { this.testMethod = testMethod; }

    public String getStandardRef() { return standardRef; }
    public void setStandardRef(String standardRef) { this.standardRef = standardRef; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}