package com.wei.lims.stability.service;

import java.util.List;
import com.wei.lims.stability.domain.LimsStabilityProtocol;

public interface ILimsStabilityProtocolService {
    LimsStabilityProtocol selectByProtocolId(Long protocolId);
    List<LimsStabilityProtocol> selectLimsStabilityProtocolList(LimsStabilityProtocol protocol);
    int insertLimsStabilityProtocol(LimsStabilityProtocol protocol);
    int updateLimsStabilityProtocol(LimsStabilityProtocol protocol);
    int deleteLimsStabilityProtocolByProtocolIds(Long[] protocolIds);
}
