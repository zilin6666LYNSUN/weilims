package com.wei.lims.training.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 培训参与人员对象 lims_training_participant
 */
public class LimsTrainingParticipant {
    private static final long serialVersionUID = 1L;

    private Long participantId;

    private Long trainingId;

    private Long staffId;

    private String staffName;

    private String attendanceStatus;

    private BigDecimal examScore;

    private Integer examPassed;

    private Integer practicalPassed;

    private String certificateNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date certificateExpire;

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public Long getTrainingId() { return trainingId; }
    public void setTrainingId(Long trainingId) { this.trainingId = trainingId; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }

    public BigDecimal getExamScore() { return examScore; }
    public void setExamScore(BigDecimal examScore) { this.examScore = examScore; }

    public Integer getExamPassed() { return examPassed; }
    public void setExamPassed(Integer examPassed) { this.examPassed = examPassed; }

    public Integer getPracticalPassed() { return practicalPassed; }
    public void setPracticalPassed(Integer practicalPassed) { this.practicalPassed = practicalPassed; }

    public String getCertificateNo() { return certificateNo; }
    public void setCertificateNo(String certificateNo) { this.certificateNo = certificateNo; }

    public Date getCertificateExpire() { return certificateExpire; }
    public void setCertificateExpire(Date certificateExpire) { this.certificateExpire = certificateExpire; }

}