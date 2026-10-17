package com.wei.lims.training.mapper;

import java.util.List;
import com.wei.lims.training.domain.LimsTrainingParticipant;

public interface LimsTrainingParticipantMapper {
    public LimsTrainingParticipant selectLimsTrainingParticipantByParticipantId(Long participantId);
    public List<LimsTrainingParticipant> selectLimsTrainingParticipantList(LimsTrainingParticipant LimsTrainingParticipant);
    public int insertLimsTrainingParticipant(LimsTrainingParticipant limstrainingparticipant);
    public int updateLimsTrainingParticipant(LimsTrainingParticipant limstrainingparticipant);
    public int deleteLimsTrainingParticipantByParticipantId(Long[] participantIds);

    public List<LimsTrainingParticipant> selectByTrainingId(Long trainingId);
}