package com.wei.lims.test.service;

import java.util.List;
import com.wei.lims.test.domain.LimsTestMethod;

/**
 * 检验方法Service接口
 */
public interface ILimsTestMethodService {

    public LimsTestMethod selectLimsTestMethodByMethodId(Long methodId);

    public List<LimsTestMethod> selectLimsTestMethodList(LimsTestMethod method);

    public int insertLimsTestMethod(LimsTestMethod method);

    public int updateLimsTestMethod(LimsTestMethod method);

    public int deleteLimsTestMethodByMethodIds(Long[] methodIds);

    public String checkMethodCodeUnique(LimsTestMethod method);
}
