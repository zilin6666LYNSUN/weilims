package com.wei.lims.test.mapper;

import java.util.List;
import com.wei.lims.test.domain.LimsTestMethod;

public interface LimsTestMethodMapper {

    public LimsTestMethod selectLimsTestMethodByMethodId(Long methodId);

    public LimsTestMethod selectLimsTestMethodByMethodCode(String methodCode);

    public List<LimsTestMethod> selectLimsTestMethodList(LimsTestMethod method);

    public int insertLimsTestMethod(LimsTestMethod method);

    public int updateLimsTestMethod(LimsTestMethod method);

    public int deleteLimsTestMethodByMethodIds(Long[] methodIds);
}
