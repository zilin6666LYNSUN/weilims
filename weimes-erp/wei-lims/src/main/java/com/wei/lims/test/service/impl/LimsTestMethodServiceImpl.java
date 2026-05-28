package com.wei.lims.test.service.impl;

import java.util.List;
import com.wei.common.constant.UserConstants;
import com.wei.common.utils.DateUtils;
import com.wei.lims.test.domain.LimsTestMethod;
import com.wei.lims.test.mapper.LimsTestMethodMapper;
import com.wei.lims.test.service.ILimsTestMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsTestMethodServiceImpl implements ILimsTestMethodService {

    @Autowired
    private LimsTestMethodMapper limsTestMethodMapper;

    @Override
    public LimsTestMethod selectLimsTestMethodByMethodId(Long methodId) {
        return limsTestMethodMapper.selectLimsTestMethodByMethodId(methodId);
    }

    @Override
    public List<LimsTestMethod> selectLimsTestMethodList(LimsTestMethod method) {
        return limsTestMethodMapper.selectLimsTestMethodList(method);
    }

    @Override
    public int insertLimsTestMethod(LimsTestMethod method) {
        method.setCreateTime(DateUtils.getNowDate());
        return limsTestMethodMapper.insertLimsTestMethod(method);
    }

    @Override
    public int updateLimsTestMethod(LimsTestMethod method) {
        method.setUpdateTime(DateUtils.getNowDate());
        return limsTestMethodMapper.updateLimsTestMethod(method);
    }

    @Override
    public int deleteLimsTestMethodByMethodIds(Long[] methodIds) {
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
