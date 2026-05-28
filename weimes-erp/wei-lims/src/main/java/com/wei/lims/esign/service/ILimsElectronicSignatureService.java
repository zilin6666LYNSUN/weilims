package com.wei.lims.esign.service;

import java.util.List;
import com.wei.lims.esign.domain.LimsElectronicSignature;

/**
 * 电子签名Service接口
 */
public interface ILimsElectronicSignatureService {

    /**
     * 创建电子签名
     */
    public Long createSignature(String recordType, Long recordId, String signatureMeaning, String reason);

    /**
     * 验证电子签名(输入密码确认)
     */
    public boolean verifySignature(Long signatureId, String password);

    /**
     * 查询签名记录
     */
    public List<LimsElectronicSignature> selectByRecordTypeAndId(String recordType, Long recordId);

    /**
     * 查询签名列表
     */
    public List<LimsElectronicSignature> selectLimsElectronicSignatureList(LimsElectronicSignature signature);

    /**
     * 作废签名
     */
    public int invalidateSignature(Long signatureId, String reason);
}
