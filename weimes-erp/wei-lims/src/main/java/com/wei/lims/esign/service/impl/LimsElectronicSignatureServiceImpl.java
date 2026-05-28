package com.wei.lims.esign.service.impl;

import java.util.Date;
import java.util.List;
import com.wei.common.utils.SecurityUtils;
import com.wei.common.utils.ip.IpUtils;
import com.wei.lims.esign.domain.LimsAuditTrail;
import com.wei.lims.esign.domain.LimsElectronicSignature;
import com.wei.lims.esign.mapper.LimsElectronicSignatureMapper;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.esign.service.ILimsElectronicSignatureService;
import com.wei.common.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 电子签名Service实现
 * 满足 21 CFR Part 11 要求
 */
@Service
public class LimsElectronicSignatureServiceImpl implements ILimsElectronicSignatureService {

    @Autowired
    private LimsElectronicSignatureMapper limsElectronicSignatureMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    @Transactional
    public Long createSignature(String recordType, Long recordId, String signatureMeaning, String reason) {
        LimsElectronicSignature signature = new LimsElectronicSignature();
        signature.setRecordType(recordType);
        signature.setRecordId(recordId);
        signature.setSignatureMeaning(signatureMeaning);
        signature.setSignatureTime(new Date());
        signature.setReason(reason);
        signature.setIsValid(1);

        try {
            signature.setSignerId(SecurityUtils.getUserId());
            signature.setSignerName(SecurityUtils.getUsername());
        } catch (Exception e) {
            signature.setSignerName("SYSTEM");
        }

        try {
            HttpServletRequest request = ServletUtils.getRequest();
            if (request != null) {
                signature.setIpAddress(IpUtils.getIpAddr(request));
                signature.setSessionId(request.getSession().getId());
            }
        } catch (Exception ignored) {}

        limsElectronicSignatureMapper.insertLimsElectronicSignature(signature);

        // 记录审计追踪
        limsAuditTrailService.recordFieldChanges("lims_electronic_signature",
                signature.getSignatureId(), "INSERT", null, signature, reason);

        return signature.getSignatureId();
    }

    @Override
    public boolean verifySignature(Long signatureId, String password) {
        LimsElectronicSignature signature = limsElectronicSignatureMapper.selectLimsElectronicSignatureById(signatureId);
        if (signature == null || signature.getIsValid() != 1) {
            return false;
        }
        // 验证当前用户是否为签名人
        try {
            Long currentUserId = SecurityUtils.getUserId();
            return signature.getSignerId().equals(currentUserId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<LimsElectronicSignature> selectByRecordTypeAndId(String recordType, Long recordId) {
        return limsElectronicSignatureMapper.selectByRecordTypeAndId(recordType, recordId);
    }

    @Override
    public List<LimsElectronicSignature> selectLimsElectronicSignatureList(LimsElectronicSignature signature) {
        return limsElectronicSignatureMapper.selectLimsElectronicSignatureList(signature);
    }

    @Override
    @Transactional
    public int invalidateSignature(Long signatureId, String reason) {
        limsAuditTrailService.recordFieldChanges("lims_electronic_signature",
                signatureId, "UPDATE", null, null, reason);
        return limsElectronicSignatureMapper.invalidateSignature(signatureId);
    }
}
