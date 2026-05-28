package com.wei.lims.report.mapper;

import java.util.List;
import com.wei.lims.report.domain.LimsReport;

/**
 * 检验报告Mapper接口
 */
public interface LimsReportMapper {

    public LimsReport selectLimsReportByReportId(Long reportId);

    public LimsReport selectLimsReportByReportCode(String reportCode);

    public List<LimsReport> selectLimsReportList(LimsReport limsReport);

    public int insertLimsReport(LimsReport limsReport);

    public int updateLimsReport(LimsReport limsReport);

    public int deleteLimsReportByReportIds(Long[] reportIds);

    public String checkReportCodeUnique(String reportCode);
}
