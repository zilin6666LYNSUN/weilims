package com.wei.lims.report.service;

import java.util.List;
import com.wei.lims.report.domain.LimsReport;

/**
 * 检验报告Service接口
 * GMP合规：编制-复核-批准-放行 四步审批
 */
public interface ILimsReportService {

    public LimsReport selectLimsReportByReportId(Long reportId);

    public List<LimsReport> selectLimsReportList(LimsReport limsReport);

    public int insertLimsReport(LimsReport limsReport);

    public int updateLimsReport(LimsReport limsReport);

    public int deleteLimsReportByReportIds(Long[] reportIds);

    public String checkReportCodeUnique(String reportCode);

    /**
     * 根据检验订单创建报告草稿
     */
    public LimsReport createReportFromTestOrder(Long testOrderId, Long templateId, String reportType);

    /**
     * 生成报告（填充数据到模板，生成PDF）
     */
    public int generateReport(Long reportId);

    /**
     * 报告复核（GMP四眼原则：复核人不能是编制人）
     */
    public int reviewReport(Long reportId, String reviewResult, String comment);

    /**
     * 报告批准（GMP四眼原则：批准人不能是复核人）
     */
    public int approveReport(Long reportId, String approveResult, String comment);

    /**
     * 报告放行
     */
    public int releaseReport(Long reportId);

    /**
     * 作废报告
     */
    public int voidReport(Long reportId, String voidReason);

    /**
     * 打印报告（记录打印次数和时间，GMP审计追踪）
     */
    public int printReport(Long reportId);
}
