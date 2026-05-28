package com.wei.lims.instrument.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 仪器校准记录对象 lims_instrument_calibration
 */
public class LimsInstrumentCalibration extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long calibrationId;

    private Long instrumentId;

    @Excel(name = "校准编号")
    private String calibrationCode;

    @Excel(name = "校准类型: INTERNAL-内部/EXTERNAL-外部")
    private String calibrationType;

    @Excel(name = "校准机构")
    private String calibrationOrg;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date calibrationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextCalibration;

    @Excel(name = "校准结果: QUALIFIED-合格/UNQUALIFIED-不合格")
    private String calibrationResult;

    @Excel(name = "校准证书号")
    private String certificateNo;

    private String certificateFile;

    private String calibratedBy;

    private String approvedBy;

    private String status;

    public Long getCalibrationId() { return calibrationId; }
    public void setCalibrationId(Long calibrationId) { this.calibrationId = calibrationId; }
    public Long getInstrumentId() { return instrumentId; }
    public void setInstrumentId(Long instrumentId) { this.instrumentId = instrumentId; }
    public String getCalibrationCode() { return calibrationCode; }
    public void setCalibrationCode(String calibrationCode) { this.calibrationCode = calibrationCode; }
    public String getCalibrationType() { return calibrationType; }
    public void setCalibrationType(String calibrationType) { this.calibrationType = calibrationType; }
    public String getCalibrationOrg() { return calibrationOrg; }
    public void setCalibrationOrg(String calibrationOrg) { this.calibrationOrg = calibrationOrg; }
    public Date getCalibrationDate() { return calibrationDate; }
    public void setCalibrationDate(Date calibrationDate) { this.calibrationDate = calibrationDate; }
    public Date getNextCalibration() { return nextCalibration; }
    public void setNextCalibration(Date nextCalibration) { this.nextCalibration = nextCalibration; }
    public String getCalibrationResult() { return calibrationResult; }
    public void setCalibrationResult(String calibrationResult) { this.calibrationResult = calibrationResult; }
    public String getCertificateNo() { return certificateNo; }
    public void setCertificateNo(String certificateNo) { this.certificateNo = certificateNo; }
    public String getCertificateFile() { return certificateFile; }
    public void setCertificateFile(String certificateFile) { this.certificateFile = certificateFile; }
    public String getCalibratedBy() { return calibratedBy; }
    public void setCalibratedBy(String calibratedBy) { this.calibratedBy = calibratedBy; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
