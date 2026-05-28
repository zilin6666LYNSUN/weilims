package com.wei.lims.test.service;

import java.util.List;
import com.wei.lims.test.domain.LimsTestResult;

/**
 * 检验结果Service接口
 */
public interface ILimsTestResultService {

    public LimsTestResult selectLimsTestResultByResultId(Long resultId);

    public List<LimsTestResult> selectLimsTestResultByTestOrderId(Long testOrderId);

    public List<LimsTestResult> selectLimsTestResultList(LimsTestResult limsTestResult);

    public int insertLimsTestResult(LimsTestResult limsTestResult);

    public int updateLimsTestResult(LimsTestResult limsTestResult);

    public int deleteLimsTestResultByResultIds(Long[] resultIds);

    public int deleteLimsTestResultByTestOrderId(Long testOrderId);
}
