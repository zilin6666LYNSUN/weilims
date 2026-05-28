package com.wei.lims.stability.mapper;

import java.util.List;
import com.wei.lims.stability.domain.LimsStabilityProtocol;

public interface LimsStabilityProtocolMapper {
    LimsStabilityProtocol selectByProtocolId(Long protocolId);
    List<LimsStabilityProtocol> selectLimsStabilityProtocolList(LimsStabilityProtocol protocol);
    int insertLimsStabilityProtocol(LimsStabilityProtocol protocol);
    int updateLimsStabilityProtocol(LimsStabilityProtocol protocol);
    int deleteLimsStabilityProtocolByProtocolIds(Long[] protocolIds);
}
