package com.wei.lims.test.service.impl;

import java.util.List;
import com.wei.common.constant.UserConstants;
import com.wei.common.utils.DateUtils;
import com.wei.lims.test.domain.LimsSpecification;
import com.wei.lims.test.domain.LimsSpecificationLine;
import com.wei.lims.test.mapper.LimsSpecificationMapper;
import com.wei.lims.test.mapper.LimsSpecificationLineMapper;
import com.wei.lims.test.service.ILimsSpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsSpecificationServiceImpl implements ILimsSpecificationService {

    @Autowired
    private LimsSpecificationMapper limsSpecificationMapper;

    @Autowired
    private LimsSpecificationLineMapper limsSpecificationLineMapper;

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
        return limsSpecificationMapper.insertLimsSpecification(specification);
    }

    @Override
    @Transactional
    public int updateLimsSpecification(LimsSpecification specification) {
        specification.setUpdateTime(DateUtils.getNowDate());
        return limsSpecificationMapper.updateLimsSpecification(specification);
    }

    @Override
    public int deleteLimsSpecificationBySpecificationIds(Long[] specificationIds) {
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
    public int insertSpecificationLine(LimsSpecificationLine line) {
        return limsSpecificationLineMapper.insertLimsSpecificationLine(line);
    }

    @Override
    public int updateSpecificationLine(LimsSpecificationLine line) {
        return limsSpecificationLineMapper.updateLimsSpecificationLine(line);
    }

    @Override
    public int deleteSpecificationLineByLineIds(Long[] lineIds) {
        return limsSpecificationLineMapper.deleteLimsSpecificationLineByLineIds(lineIds);
    }
}
