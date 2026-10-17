package com.wei.lims.stability.service.impl;

import java.util.List;
import com.wei.lims.stability.domain.LimsStabilityProtocol;
import com.wei.lims.stability.mapper.LimsStabilityProtocolMapper;
import com.wei.lims.stability.service.ILimsStabilityProtocolService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsStabilityProtocolServiceImpl implements ILimsStabilityProtocolService {

    @Autowired
    private LimsStabilityProtocolMapper limsStabilityProtocolMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsStabilityProtocol selectByProtocolId(Long protocolId) {
        return limsStabilityProtocolMapper.selectByProtocolId(protocolId);
    }

    @Override
    public List<LimsStabilityProtocol> selectLimsStabilityProtocolList(LimsStabilityProtocol protocol) {
        return limsStabilityProtocolMapper.selectLimsStabilityProtocolList(protocol);
    }

    @Override
    @Transactional
    public int insertLimsStabilityProtocol(LimsStabilityProtocol protocol) {
        int rows = limsStabilityProtocolMapper.insertLimsStabilityProtocol(protocol);
        limsAuditTrailService.recordFieldChanges("lims_stability_protocol", protocol.getProtocolId(),
                "INSERT", null, protocol, "新建稳定性方案");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsStabilityProtocol(LimsStabilityProtocol protocol) {
        LimsStabilityProtocol oldProtocol = limsStabilityProtocolMapper.selectByProtocolId(protocol.getProtocolId());
        int rows = limsStabilityProtocolMapper.updateLimsStabilityProtocol(protocol);
        limsAuditTrailService.recordFieldChanges("lims_stability_protocol", protocol.getProtocolId(),
                "UPDATE", oldProtocol, protocol, "更新稳定性方案");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsStabilityProtocolByProtocolIds(Long[] protocolIds) {
        for (Long protocolId : protocolIds) {
            limsAuditTrailService.recordFieldChanges("lims_stability_protocol", protocolId,
                    "DELETE", null, null, "删除稳定性方案");
        }
        return limsStabilityProtocolMapper.deleteLimsStabilityProtocolByProtocolIds(protocolIds);
    }
}
