package com.wei.lims.test.service.impl;

import java.util.List;
import com.wei.common.constant.UserConstants;
import com.wei.common.utils.DateUtils;
import com.wei.lims.test.domain.LimsSpecification;
import com.wei.lims.test.domain.LimsSpecificationLine;
import com.wei.lims.test.mapper.LimsSpecificationMapper;
import com.wei.lims.test.mapper.LimsSpecificationLineMapper;
import com.wei.lims.test.service.ILimsSpecificationService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsSpecificationServiceImpl implements ILimsSpecificationService {

    @Autowired
    private LimsSpecificationMapper limsSpecificationMapper;

    @Autowired
    private LimsSpecificationLineMapper limsSpecificationLineMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsSpecification selectLimsSpecificationBySpecificationId(Long specificationId) {
        return limsSpecificationMapper.selectLimsSpecificationBySpecificationId(specificationId);
    }

    @Override
    public List<LimsSpecification> selectLimsSpecificationList(LimsSpecification specification) {
        return limsSpecificationMapper.selectLimsSpecificationList(specification);
    }

    @Override
    @Transactional
    public int insertLimsSpecification(LimsSpecification specification) {
        specification.setCreateTime(DateUtils.getNowDate());
        int rows = limsSpecificationMapper.insertLimsSpecification(specification);
        limsAuditTrailService.recordFieldChanges("lims_specification", specification.getSpecificationId(),
                "INSERT", null, specification, "新建质量标准");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsSpecification(LimsSpecification specification) {
        specification.setUpdateTime(DateUtils.getNowDate());
        LimsSpecification oldSpec = limsSpecificationMapper.selectLimsSpecificationBySpecificationId(specification.getSpecificationId());
        int rows = limsSpecificationMapper.updateLimsSpecification(specification);
        limsAuditTrailService.recordFieldChanges("lims_specification", specification.getSpecificationId(),
                "UPDATE", oldSpec, specification, "更新质量标准");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsSpecificationBySpecificationIds(Long[] specificationIds) {
        for (Long specificationId : specificationIds) {
            LimsSpecification oldSpec = limsSpecificationMapper.selectLimsSpecificationBySpecificationId(specificationId);
            limsAuditTrailService.recordFieldChanges("lims_specification", specificationId,
                    "DELETE", oldSpec, null, "删除质量标准");
        }
        return limsSpecificationMapper.deleteLimsSpecificationBySpecificationIds(specificationIds);
    }

    @Override
    public String checkSpecificationCodeUnique(LimsSpecification specification) {
        LimsSpecification existing = limsSpecificationMapper.selectLimsSpecificationBySpecificationCode(specification.getSpecificationCode());
        if (existing != null && !existing.getSpecificationId().equals(specification.getSpecificationId())) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public List<LimsSpecificationLine> selectLinesBySpecificationId(Long specificationId) {
        return limsSpecificationLineMapper.selectBySpecificationId(specificationId);
    }

    @Override
    @Transactional
    public int insertSpecificationLine(LimsSpecificationLine line) {
        int rows = limsSpecificationLineMapper.insertLimsSpecificationLine(line);
        limsAuditTrailService.recordFieldChanges("lims_specification_line", line.getLineId(),
                "INSERT", null, line, "新建质量标准明细");
        return rows;
    }

    @Override
    @Transactional
    public int updateSpecificationLine(LimsSpecificationLine line) {
        LimsSpecificationLine oldLine = limsSpecificationLineMapper.selectByLineId(line.getLineId());
        int rows = limsSpecificationLineMapper.updateLimsSpecificationLine(line);
        limsAuditTrailService.recordFieldChanges("lims_specification_line", line.getLineId(),
                "UPDATE", oldLine, line, "更新质量标准明细");
        return rows;
    }

    @Override
    @Transactional
    public int deleteSpecificationLineByLineIds(Long[] lineIds) {
        for (Long lineId : lineIds) {
            LimsSpecificationLine oldLine = limsSpecificationLineMapper.selectByLineId(lineId);
            limsAuditTrailService.recordFieldChanges("lims_specification_line", lineId,
                    "DELETE", oldLine, null, "删除质量标准明细");
        }
        return limsSpecificationLineMapper.deleteLimsSpecificationLineByLineIds(lineIds);
    }
}
