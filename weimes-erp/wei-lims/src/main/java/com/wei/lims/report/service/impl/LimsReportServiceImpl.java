package com.wei.lims.report.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.wei.common.exception.BusinessException;
import com.wei.common.utils.DateUtils;
import com.wei.common.utils.SecurityUtils;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.process.service.ILimsProcessService;
import com.wei.lims.report.domain.LimsReport;
import com.wei.lims.report.domain.LimsReportSignature;
import com.wei.lims.report.mapper.LimsReportMapper;
import com.wei.lims.report.mapper.LimsReportSignatureMapper;
import com.wei.lims.report.service.ILimsDataValidationService;
import com.wei.lims.report.service.ILimsReportService;
import com.wei.lims.test.domain.LimsTestOrder;
import com.wei.lims.test.domain.LimsTestResult;
import com.wei.lims.test.mapper.LimsTestOrderMapper;
import com.wei.lims.test.mapper.LimsTestResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 检验报告Service实现
 * GMP合规：编制-复核-批准-放行 四步审批流程
 * 四眼原则：同一人不能同时担任编制和复核、复核和批准角色
 */
@Service
public class LimsReportServiceImpl implements ILimsReportService {

    @Autowired
    private LimsReportMapper limsReportMapper;

    @Autowired
    private LimsReportSignatureMapper limsReportSignatureMapper;

    @Autowired
    private LimsTestOrderMapper limsTestOrderMapper;

    @Autowired
    private LimsTestResultMapper limsTestResultMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Autowired
    private ILimsDataValidationService limsDataValidationService;

    @Autowired
    private ILimsProcessService limsProcessService;

    @Override
    public LimsReport selectLimsReportByReportId(Long reportId) {
        return limsReportMapper.selectLimsReportByReportId(reportId);
    }

    @Override
    public List<LimsReport> selectLimsReportList(LimsReport limsReport) {
        return limsReportMapper.selectLimsReportList(limsReport);
    }

    @Override
    @Transactional
    public int insertLimsReport(LimsReport limsReport) {
        limsReport.setCreateTime(DateUtils.getNowDate());
        int rows = limsReportMapper.insertLimsReport(limsReport);
        limsAuditTrailService.recordFieldChanges("lims_report", limsReport.getReportId(),
                "INSERT", null, limsReport, "新建检验报告");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsReport(LimsReport limsReport) {
        LimsReport old = limsReportMapper.selectLimsReportByReportId(limsReport.getReportId());
        limsReport.setUpdateTime(DateUtils.getNowDate());
        limsReport.setUpdateBy(SecurityUtils.getUsername());
        int rows = limsReportMapper.updateLimsReport(limsReport);
        limsAuditTrailService.recordFieldChanges("lims_report", limsReport.getReportId(),
                "UPDATE", old, limsReport, "更新检验报告");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsReportByReportIds(Long[] reportIds) {
        for (Long id : reportIds) {
            limsAuditTrailService.recordFieldChanges("lims_report", id,
                    "DELETE", null, null, "删除检验报告");
        }
        return limsReportMapper.deleteLimsReportByReportIds(reportIds);
    }

    @Override
    public String checkReportCodeUnique(String reportCode) {
        LimsReport existing = limsReportMapper.selectLimsReportByReportCode(reportCode);
        return existing != null ? "1" : "0";
    }

    @Override
    @Transactional
    public LimsReport createReportFromTestOrder(Long testOrderId, Long templateId, String reportType) {
        LimsTestOrder order = limsTestOrderMapper.selectLimsTestOrderByTestOrderId(testOrderId);
        if (order == null) {
            throw new BusinessException("检验订单不存在");
        }
        if (!"APPROVED".equals(order.getStatus())) {
            throw new BusinessException("检验订单未批准，无法创建报告");
        }

        // 生成报告编号
        String reportCode = "RPT-" + DateUtils.dateTimeNow("yyyyMMdd") + "-" + String.format("%04d", System.currentTimeMillis() % 10000);

        LimsReport report = new LimsReport();
        report.setReportCode(reportCode);
        report.setReportType(reportType);
        report.setTemplateId(templateId);
        report.setBusinessType("TEST_ORDER");
        report.setBusinessId(testOrderId);
        report.setTitle(order.getTestOrderName() + "检验报告");
        report.setProductName(order.getSampleCode());
        report.setStatus("DRAFT");
        report.setVersion(1);
        report.setPrintedCount(0);
        report.setGeneratedBy(SecurityUtils.getUserId());
        report.setGeneratedTime(new Date());
        report.setCreateBy(SecurityUtils.getUsername());
        report.setCreateTime(DateUtils.getNowDate());

        limsReportMapper.insertLimsReport(report);

        // 记录编制签章（先删后插，防止唯一约束冲突）
        limsReportSignatureMapper.deleteSignatureByReportAndType(report.getReportId(), "PREPARE");
        LimsReportSignature signature = new LimsReportSignature();
        signature.setReportId(report.getReportId());
        signature.setSignatureType("PREPARE");
        signature.setSignerId(SecurityUtils.getUserId());
        signature.setSignerName(SecurityUtils.getUsername());
        signature.setSignatureMeaning("报告编制");
        limsReportSignatureMapper.insertSignature(signature);

        limsAuditTrailService.recordFieldChanges("lims_report", report.getReportId(),
                "INSERT", null, report, "从检验订单创建报告");

        return report;
    }

    @Override
    @Transactional
    public int generateReport(Long reportId) {
        LimsReport report = limsReportMapper.selectLimsReportByReportId(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }

        // GMP合规：生成报告前必须通过数据校验
        Long testOrderId = report.getBusinessId();
        if (testOrderId != null && !limsDataValidationService.isAllValidationPassed(testOrderId)) {
            throw new BusinessException("GMP合规：数据校验未全部通过，无法生成报告");
        }

        // 收集检验结果数据
        List<LimsTestResult> results = limsTestResultMapper.selectLimsTestResultByTestOrderId(testOrderId);

        // 构建报告数据JSON（简化实现，实际应根据模板配置填充）
        StringBuilder jsonData = new StringBuilder("{");
        jsonData.append("\"reportCode\":\"").append(report.getReportCode()).append("\",");
        jsonData.append("\"testOrderId\":").append(testOrderId).append(",");
        jsonData.append("\"resultCount\":").append(results.size()).append(",");
        jsonData.append("\"items\":[");

        String conclusion = "QUALIFIED";
        for (int i = 0; i < results.size(); i++) {
            LimsTestResult r = results.get(i);
            if (i > 0) jsonData.append(",");
            jsonData.append("{");
            jsonData.append("\"itemName\":\"").append(r.getTestItemName()).append("\",");
            jsonData.append("\"reportedValue\":\"").append(r.getReportedValue() != null ? r.getReportedValue() : "").append("\",");
            jsonData.append("\"specification\":\"").append(r.getSpecificationVal() != null ? r.getSpecificationVal() : "").append("\",");
            jsonData.append("\"conclusion\":\"").append(r.getResultConclusion() != null ? r.getResultConclusion() : "").append("\"");
            jsonData.append("}");

            if ("UNQUALIFIED".equals(r.getResultConclusion())) {
                conclusion = "UNQUALIFIED";
            }
        }
        jsonData.append("]}");

        report.setReportData(jsonData.toString());
        report.setConclusion(conclusion);
        report.setStatus("PREPARING");
        report.setGeneratedBy(SecurityUtils.getUserId());
        report.setGeneratedTime(new Date());
        report.setUpdateBy(SecurityUtils.getUsername());
        report.setUpdateTime(DateUtils.getNowDate());

        // P1可配置审批流：若已配置报告放行流程定义(REPORT_RELEASE_FLOW)，生成报告时启动流程实例并回填；
        // 未配置或配置异常时回退原硬编码状态流转，保证既有功能不破坏。
        if (limsProcessService.hasActiveProcessDefinition("REPORT_RELEASE_FLOW")) {
            try {
                ILimsProcessService.ProcessInstanceResult result = limsProcessService.startProcess(
                        "REPORT_RELEASE_FLOW", "REPORT", report.getReportId(), null);
                report.setProcInstId(result.getProcInstId());
            } catch (BusinessException e) {
                // 流程配置异常时静默回退硬编码流转，不阻断报告生成
            }
        }

        int rows = limsReportMapper.updateLimsReport(report);
        limsAuditTrailService.recordFieldChanges("lims_report", reportId,
                "UPDATE", null, report, "生成报告数据");
        return rows;
    }

    @Override
    @Transactional
    public int reviewReport(Long reportId, String reviewResult, String comment) {
        LimsReport report = limsReportMapper.selectLimsReportByReportId(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        if (!"PREPARING".equals(report.getStatus()) && !"DRAFT".equals(report.getStatus())) {
            throw new BusinessException("报告状态不允许复核");
        }

        // GMP四眼原则：复核人不能是编制人
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId.equals(report.getGeneratedBy())) {
            throw new BusinessException("GMP合规：复核人不能与编制人为同一人（四眼原则）");
        }

        if ("PASS".equals(reviewResult)) {
            // P1可配置审批流：实例已启动则经引擎推进复核节点，业务状态按当前节点 businessStatus 回填；
            // 未启动实例保持原硬编码流转
            if (report.getProcInstId() != null && !report.getProcInstId().isEmpty()) {
                limsProcessService.completeTask(report.getProcInstId(), null, "APPROVE", comment, null);
                report.setStatus(resolveBusinessStatus(report.getProcInstId(), "REVIEWING"));
            } else {
                report.setStatus("REVIEWING");
            }
            report.setReviewedBy(currentUserId);
            report.setReviewedTime(new Date());
            report.setReviewComment(comment);

            // 记录复核签章（先删后插，防止唯一约束冲突）
            limsReportSignatureMapper.deleteSignatureByReportAndType(reportId, "REVIEW");
            LimsReportSignature signature = new LimsReportSignature();
            signature.setReportId(reportId);
            signature.setSignatureType("REVIEW");
            signature.setSignerId(currentUserId);
            signature.setSignerName(SecurityUtils.getUsername());
            signature.setSignatureMeaning("报告复核通过");
            signature.setReason(comment);
            limsReportSignatureMapper.insertSignature(signature);
        } else {
            // P1可配置审批流：引擎按 nextOnReject 回退，业务状态回填；未启动实例保持原硬编码回退
            if (report.getProcInstId() != null && !report.getProcInstId().isEmpty()) {
                limsProcessService.completeTask(report.getProcInstId(), null, "REJECT", comment, null);
                report.setStatus(resolveBusinessStatus(report.getProcInstId(), "DRAFT"));
            } else {
                report.setStatus("DRAFT");
            }
            report.setReviewComment(comment);

            limsAuditTrailService.recordFieldChanges("lims_report", reportId,
                    "UPDATE", null, report, "报告复核退回: " + comment);
        }

        report.setUpdateBy(SecurityUtils.getUsername());
        report.setUpdateTime(DateUtils.getNowDate());

        int rows = limsReportMapper.updateLimsReport(report);
        limsAuditTrailService.recordFieldChanges("lims_report", reportId,
                "UPDATE", null, report, "报告复核: " + reviewResult);
        return rows;
    }

    @Override
    @Transactional
    public int approveReport(Long reportId, String approveResult, String comment) {
        LimsReport report = limsReportMapper.selectLimsReportByReportId(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        if (!"REVIEWING".equals(report.getStatus())) {
            throw new BusinessException("报告状态不允许批准");
        }

        // GMP四眼原则：批准人不能是复核人，也不能是编制人
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId.equals(report.getReviewedBy())) {
            throw new BusinessException("GMP合规：批准人不能与复核人为同一人（四眼原则）");
        }
        if (currentUserId.equals(report.getGeneratedBy())) {
            throw new BusinessException("GMP合规：批准人不能与编制人为同一人（四眼原则）");
        }

        if ("PASS".equals(approveResult)) {
            // P1可配置审批流：经引擎推进批准节点；未启动实例保持原硬编码流转
            if (report.getProcInstId() != null && !report.getProcInstId().isEmpty()) {
                limsProcessService.completeTask(report.getProcInstId(), null, "APPROVE", comment, null);
                report.setStatus(resolveBusinessStatus(report.getProcInstId(), "APPROVING"));
            } else {
                report.setStatus("APPROVING");
            }
            report.setApprovedBy(currentUserId);
            report.setApprovedTime(new Date());
            report.setApproveComment(comment);

            // 记录批准签章（先删后插，防止唯一约束冲突）
            limsReportSignatureMapper.deleteSignatureByReportAndType(reportId, "APPROVE");
            LimsReportSignature signature = new LimsReportSignature();
            signature.setReportId(reportId);
            signature.setSignatureType("APPROVE");
            signature.setSignerId(currentUserId);
            signature.setSignerName(SecurityUtils.getUsername());
            signature.setSignatureMeaning("报告批准通过");
            signature.setReason(comment);
            limsReportSignatureMapper.insertSignature(signature);
        } else {
            // P1可配置审批流：引擎按 nextOnReject 回退；未启动实例保持原硬编码回退
            if (report.getProcInstId() != null && !report.getProcInstId().isEmpty()) {
                limsProcessService.completeTask(report.getProcInstId(), null, "REJECT", comment, null);
                report.setStatus(resolveBusinessStatus(report.getProcInstId(), "PREPARING"));
            } else {
                report.setStatus("PREPARING");
            }
            report.setApproveComment(comment);

            limsAuditTrailService.recordFieldChanges("lims_report", reportId,
                    "UPDATE", null, report, "报告批准退回: " + comment);
        }

        report.setUpdateBy(SecurityUtils.getUsername());
        report.setUpdateTime(DateUtils.getNowDate());

        int rows = limsReportMapper.updateLimsReport(report);
        limsAuditTrailService.recordFieldChanges("lims_report", reportId,
                "UPDATE", null, report, "报告批准: " + approveResult);
        return rows;
    }

    @Override
    @Transactional
    public int releaseReport(Long reportId) {
        LimsReport report = limsReportMapper.selectLimsReportByReportId(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        if (!"APPROVING".equals(report.getStatus())) {
            throw new BusinessException("报告状态不允许放行");
        }

        Long currentUserId = SecurityUtils.getUserId();
        // GMP四眼原则：放行人不能是批准人、复核人或编制人
        if (currentUserId.equals(report.getApprovedBy())) {
            throw new BusinessException("GMP合规：放行人不能与批准人为同一人（四眼原则）");
        }
        if (currentUserId.equals(report.getReviewedBy())) {
            throw new BusinessException("GMP合规：放行人不能与复核人为同一人（四眼原则）");
        }
        if (currentUserId.equals(report.getGeneratedBy())) {
            throw new BusinessException("GMP合规：放行人不能与编制人为同一人（四眼原则）");
        }
        // P1可配置审批流：实例已存在则先推进放行节点(若流程已结束则直接放行)，再置业务终态
        if (report.getProcInstId() != null && !report.getProcInstId().isEmpty()) {
            Map<String, Object> taskInfo = limsProcessService.getCurrentTaskInfo(report.getProcInstId());
            if (!"COMPLETED".equals(taskInfo.get("instanceStatus"))) {
                limsProcessService.completeTask(report.getProcInstId(), null, "APPROVE", "报告放行", null);
            }
        }
        report.setStatus("RELEASED");
        report.setReleasedBy(currentUserId);
        report.setReleasedTime(new Date());
        report.setUpdateBy(SecurityUtils.getUsername());
        report.setUpdateTime(DateUtils.getNowDate());

        // 记录放行签章（先删后插，防止唯一约束冲突）
        limsReportSignatureMapper.deleteSignatureByReportAndType(reportId, "RELEASE");
        LimsReportSignature signature = new LimsReportSignature();
        signature.setReportId(reportId);
        signature.setSignatureType("RELEASE");
        signature.setSignerId(currentUserId);
        signature.setSignerName(SecurityUtils.getUsername());
        signature.setSignatureMeaning("报告放行");
        limsReportSignatureMapper.insertSignature(signature);

        int rows = limsReportMapper.updateLimsReport(report);
        limsAuditTrailService.recordFieldChanges("lims_report", reportId,
                "UPDATE", null, report, "报告放行");
        return rows;
    }

    @Override
    @Transactional
    public int voidReport(Long reportId, String voidReason) {
        LimsReport report = limsReportMapper.selectLimsReportByReportId(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        if ("RELEASED".equals(report.getStatus())) {
            throw new BusinessException("已放行的报告不能直接作废，请创建修订版");
        }

        // P1可配置审批流：作废时终止仍在运行的流程实例
        if (report.getProcInstId() != null && !report.getProcInstId().isEmpty()) {
            try {
                Map<String, Object> taskInfo = limsProcessService.getCurrentTaskInfo(report.getProcInstId());
                if ("RUNNING".equals(taskInfo.get("instanceStatus"))) {
                    limsProcessService.terminateProcess(report.getProcInstId(), voidReason);
                }
            } catch (BusinessException ignored) {
                // 实例已结束或状态异常时忽略
            }
        }

        report.setStatus("VOIDED");
        report.setVoidReason(voidReason);
        report.setUpdateBy(SecurityUtils.getUsername());
        report.setUpdateTime(DateUtils.getNowDate());

        int rows = limsReportMapper.updateLimsReport(report);
        limsAuditTrailService.recordFieldChanges("lims_report", reportId,
                "UPDATE", null, report, "报告作废: " + voidReason);
        return rows;
    }

    @Override
    @Transactional
    public int printReport(Long reportId) {
        LimsReport report = limsReportMapper.selectLimsReportByReportId(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        if (!"RELEASED".equals(report.getStatus())) {
            throw new BusinessException("只有已放行的报告才能打印");
        }

        report.setPrintedCount(report.getPrintedCount() != null ? report.getPrintedCount() + 1 : 1);
        report.setLastPrintedTime(new Date());
        report.setUpdateBy(SecurityUtils.getUsername());
        report.setUpdateTime(DateUtils.getNowDate());

        int rows = limsReportMapper.updateLimsReport(report);
        limsAuditTrailService.recordFieldChanges("lims_report", reportId,
                "UPDATE", null, report, "打印报告，累计打印次数: " + report.getPrintedCount());
        return rows;
    }

    /**
     * 从流程引擎当前节点回填业务状态：优先取流程定义中节点配置的 businessStatus，
     * 未配置则使用调用方默认状态（保持原硬编码语义）。
     */
    private String resolveBusinessStatus(String procInstId, String defaultStatus) {
        try {
            Map<String, Object> taskInfo = limsProcessService.getCurrentTaskInfo(procInstId);
            Object businessStatus = taskInfo.get("businessStatus");
            if (businessStatus != null && !businessStatus.toString().isEmpty()) {
                return businessStatus.toString();
            }
        } catch (BusinessException ignored) {
            // 实例已结束等场景使用默认状态
        }
        return defaultStatus;
    }
}
