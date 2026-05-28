package com.wei.lims.stability.service.impl;

import java.util.List;
import com.wei.lims.stability.domain.LimsStabilityProtocol;
import com.wei.lims.stability.mapper.LimsStabilityProtocolMapper;
import com.wei.lims.stability.service.ILimsStabilityProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsStabilityProtocolServiceImpl implements ILimsStabilityProtocolService {

    @Autowired
    private LimsStabilityProtocolMapper limsStabilityProtocolMapper;

    @Override
    public LimsStabilityProtocol selectByProtocolId(Long protocolId) {
        return limsStabilityProtocolMapper.selectByProtocolId(protocolId);
    }

    @Override
    public List<LimsStabilityProtocol> selectLimsStabilityProtocolList(LimsStabilityProtocol protocol) {
        return limsStabilityProtocolMapper.selectLimsStabilityProtocolList(protocol);
    }

    @Override
    public int insertLimsStabilityProtocol(LimsStabilityProtocol protocol) {
        return limsStabilityProtocolMapper.insertLimsStabilityProtocol(protocol);
    }

    @Override
    public int updateLimsStabilityProtocol(LimsStabilityProtocol protocol) {
        return limsStabilityProtocolMapper.updateLimsStabilityProtocol(protocol);
    }

    @Override
    public int deleteLimsStabilityProtocolByProtocolIds(Long[] protocolIds) {
        return limsStabilityProtocolMapper.deleteLimsStabilityProtocolByProtocolIds(protocolIds);
    }
}
