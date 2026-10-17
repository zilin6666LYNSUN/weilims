package com.wei.lims.training.service.impl;

import java.util.List;
import com.wei.lims.training.domain.LimsTrainingParticipant;
import com.wei.lims.training.mapper.LimsTrainingParticipantMapper;
import com.wei.lims.training.service.ILimsTrainingParticipantService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsTrainingParticipantServiceImpl implements ILimsTrainingParticipantService {

    @Autowired
    private LimsTrainingParticipantMapper limstrainingparticipantMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsTrainingParticipant selectLimsTrainingParticipantByParticipantId(Long participantId) {
        return limstrainingparticipantMapper.selectLimsTrainingParticipantByParticipantId(participantId);
    }

    @Override
    public List<LimsTrainingParticipant> selectLimsTrainingParticipantList(LimsTrainingParticipant limstrainingparticipant) {
        return limstrainingparticipantMapper.selectLimsTrainingParticipantList(limstrainingparticipant);
    }

    @Override
    @Transactional
    public int insertLimsTrainingParticipant(LimsTrainingParticipant limstrainingparticipant) {
        int rows = limstrainingparticipantMapper.insertLimsTrainingParticipant(limstrainingparticipant);
        limsAuditTrailService.recordFieldChanges("lims_training_participant", limstrainingparticipant.getParticipantId(), "INSERT", null, limstrainingparticipant, "新建培训参与人员");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsTrainingParticipant(LimsTrainingParticipant limstrainingparticipant) {
        LimsTrainingParticipant old = limstrainingparticipantMapper.selectLimsTrainingParticipantByParticipantId(limstrainingparticipant.getParticipantId());
        int rows = limstrainingparticipantMapper.updateLimsTrainingParticipant(limstrainingparticipant);
        limsAuditTrailService.recordFieldChanges("lims_training_participant", limstrainingparticipant.getParticipantId(), "UPDATE", old, limstrainingparticipant, "更新培训参与人员");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsTrainingParticipantByParticipantId(Long[] participantIds) {
        for (Long participantId : participantIds) {
            LimsTrainingParticipant old = limstrainingparticipantMapper.selectLimsTrainingParticipantByParticipantId(participantId);
            limsAuditTrailService.recordFieldChanges("lims_training_participant", participantId, "DELETE", old, null, "删除培训参与人员");
        }
        return limstrainingparticipantMapper.deleteLimsTrainingParticipantByParticipantId(participantIds);
    }

    @Override
    public List<LimsTrainingParticipant> selectByTrainingId(Long trainingId) {
        return limstrainingparticipantMapper.selectByTrainingId(trainingId);
    }
}