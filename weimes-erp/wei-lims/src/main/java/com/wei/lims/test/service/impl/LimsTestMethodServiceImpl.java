package com.wei.lims.test.service.impl;

import java.util.List;
import com.wei.common.constant.UserConstants;
import com.wei.common.utils.DateUtils;
import com.wei.lims.test.domain.LimsTestMethod;
import com.wei.lims.test.mapper.LimsTestMethodMapper;
import com.wei.lims.test.service.ILimsTestMethodService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsTestMethodServiceImpl implements ILimsTestMethodService {

    @Autowired
    private LimsTestMethodMapper limsTestMethodMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsTestMethod selectLimsTestMethodByMethodId(Long methodId) {
        return limsTestMethodMapper.selectLimsTestMethodByMethodId(methodId);
    }

    @Override
    public List<LimsTestMethod> selectLimsTestMethodList(LimsTestMethod method) {
        return limsTestMethodMapper.selectLimsTestMethodList(method);
    }

    @Override
    @Transactional
    public int insertLimsTestMethod(LimsTestMethod method) {
        method.setCreateTime(DateUtils.getNowDate());
        int rows = limsTestMethodMapper.insertLimsTestMethod(method);
        limsAuditTrailService.recordFieldChanges("lims_test_method", method.getMethodId(),
                "INSERT", null, method, "新建检验方法");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsTestMethod(LimsTestMethod method) {
        method.setUpdateTime(DateUtils.getNowDate());
        LimsTestMethod oldMethod = limsTestMethodMapper.selectLimsTestMethodByMethodId(method.getMethodId());
        int rows = limsTestMethodMapper.updateLimsTestMethod(method);
        limsAuditTrailService.recordFieldChanges("lims_test_method", method.getMethodId(),
                "UPDATE", oldMethod, method, "更新检验方法");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsTestMethodByMethodIds(Long[] methodIds) {
        for (Long methodId : methodIds) {
            LimsTestMethod oldMethod = limsTestMethodMapper.selectLimsTestMethodByMethodId(methodId);
            limsAuditTrailService.recordFieldChanges("lims_test_method", methodId,
                    "DELETE", oldMethod, null, "删除检验方法");
        }
        return limsTestMethodMapper.deleteLimsTestMethodByMethodIds(methodIds);
    }

    @Override
    public String checkMethodCodeUnique(LimsTestMethod method) {
        LimsTestMethod existing = limsTestMethodMapper.selectLimsTestMethodByMethodCode(method.getMethodCode());
        if (existing != null && !existing.getMethodId().equals(method.getMethodId())) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}
