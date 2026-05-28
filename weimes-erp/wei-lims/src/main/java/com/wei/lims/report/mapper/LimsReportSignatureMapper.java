package com.wei.lims.report.mapper;

import java.util.List;
import com.wei.lims.report.domain.LimsReportSignature;
import org.apache.ibatis.annotations.Param;

/**
 * 报告签章Mapper接口
 */
public interface LimsReportSignatureMapper {

    public LimsReportSignature selectSignatureById(Long signatureId);

    public List<LimsReportSignature> selectSignaturesByReportId(Long reportId);

    public List<LimsReportSignature> selectSignatureList(LimsReportSignature signature);

    public int insertSignature(LimsReportSignature signature);

    public int invalidateSignature(Long signatureId);

    /** 删除指定报告的指定类型签名（用于重新签名前清理） */
    public int deleteSignatureByReportAndType(@Param("reportId") Long reportId, @Param("signatureType") String signatureType);
}
