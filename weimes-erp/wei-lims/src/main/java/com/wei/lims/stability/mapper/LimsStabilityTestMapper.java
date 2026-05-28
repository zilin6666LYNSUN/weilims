package com.wei.lims.stability.mapper;

import java.util.List;
import com.wei.lims.stability.domain.LimsStabilityTest;

public interface LimsStabilityTestMapper {
    LimsStabilityTest selectByStabilityTestId(Long stabilityTestId);
    List<LimsStabilityTest> selectByProtocolId(Long protocolId);
    List<LimsStabilityTest> selectLimsStabilityTestList(LimsStabilityTest test);
    int insertLimsStabilityTest(LimsStabilityTest test);
    int updateLimsStabilityTest(LimsStabilityTest test);
    int deleteLimsStabilityTestByStabilityTestIds(Long[] stabilityTestIds);
}
