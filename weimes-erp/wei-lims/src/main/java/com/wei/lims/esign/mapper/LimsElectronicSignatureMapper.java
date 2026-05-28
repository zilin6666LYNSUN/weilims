package com.wei.lims.esign.mapper;

import java.util.List;
import com.wei.lims.esign.domain.LimsElectronicSignature;

/**
 * 电子签名Mapper接口
 */
public interface LimsElectronicSignatureMapper {

    public int insertLimsElectronicSignature(LimsElectronicSignature signature);

    public LimsElectronicSignature selectLimsElectronicSignatureById(Long signatureId);

    public List<LimsElectronicSignature> selectLimsElectronicSignatureList(LimsElectronicSignature signature);

    public List<LimsElectronicSignature> selectByRecordTypeAndId(String recordType, Long recordId);

    public int invalidateSignature(Long signatureId);
}
