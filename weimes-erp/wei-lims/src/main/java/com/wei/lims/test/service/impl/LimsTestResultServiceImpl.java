package com.wei.lims.test.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.test.domain.LimsTestResult;
import com.wei.lims.test.mapper.LimsTestResultMapper;
import com.wei.lims.test.service.ILimsTestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsTestResultServiceImpl implements ILimsTestResultService {

    @Autowired
    private LimsTestResultMapper limsTestResultMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsTestResult selectLimsTestResultByResultId(Long resultId) {
        return limsTestResultMapper.selectLimsTestResultByResultId(resultId);
    }

    @Override
    public List<LimsTestResult> selectLimsTestResultByTestOrderId(Long testOrderId) {
        return limsTestResultMapper.selectLimsTestResultByTestOrderId(testOrderId);
    }

    @Override
    public List<LimsTestResult> selectLimsTestResultList(LimsTestResult limsTestResult) {
        return limsTestResultMapper.selectLimsTestResultList(limsTestResult);
    }

    @Override
    @Transactional
    public int insertLimsTestResult(LimsTestResult limsTestResult) {
        limsTestResult.setCreateTime(DateUtils.getNowDate());
        int rows = limsTestResultMapper.insertLimsTestResult(limsTestResult);
        limsAuditTrailService.recordFieldChanges("lims_test_result", limsTestResult.getResultId(),
                "INSERT", null, limsTestResult, "录入检验结果");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsTestResult(LimsTestResult limsTestResult) {
        LimsTestResult old = limsTestResultMapper.selectLimsTestResultByResultId(limsTestResult.getResultId());
        limsTestResult.setUpdateTime(DateUtils.getNowDate());
        int rows = limsTestResultMapper.updateLimsTestResult(limsTestResult);
        limsAuditTrailService.recordFieldChanges("lims_test_result", limsTestResult.getResultId(),
                "UPDATE", old, limsTestResult, "更新检验结果");
        return rows;
    }

    @Override
    public int deleteLimsTestResultByResultIds(Long[] resultIds) {
        return limsTestResultMapper.deleteLimsTestResultByResultIds(resultIds);
    }

    @Override
    public int deleteLimsTestResultByTestOrderId(Long testOrderId) {
        return limsTestResultMapper.deleteLimsTestResultByTestOrderId(testOrderId);
    }
}
