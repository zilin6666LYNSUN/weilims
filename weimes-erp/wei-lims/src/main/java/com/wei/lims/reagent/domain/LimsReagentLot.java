package com.wei.lims.reagent.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;

/**
 * 试剂批次效期对象 lims_reagent_lot
 */
public class LimsReagentLot {
    private static final long serialVersionUID = 1L;

    private Long lotId;

    private Long reagentId;

    @Excel(name = "批号")
    private String lotNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date receivedDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;

    private BigDecimal quantity;

    private BigDecimal remaining;

    @Excel(name = "CoA文件")
    private String coaFile;

    @Excel(name = "状态")
    private String status;

    public Long getLotId() { return lotId; }
    public void setLotId(Long lotId) { this.lotId = lotId; }
    public Long getReagentId() { return reagentId; }
    public void setReagentId(Long reagentId) { this.reagentId = reagentId; }
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }
    public Date getReceivedDate() { return receivedDate; }
    public void setReceivedDate(Date receivedDate) { this.receivedDate = receivedDate; }
    public Date getExpireDate() { return expireDate; }
    public void setExpireDate(Date expireDate) { this.expireDate = expireDate; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getRemaining() { return remaining; }
    public void setRemaining(BigDecimal remaining) { this.remaining = remaining; }
    public String getCoaFile() { return coaFile; }
    public void setCoaFile(String coaFile) { this.coaFile = coaFile; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
