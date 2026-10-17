package com.wei.lims.reagent.service.impl;

import java.util.List;
import com.wei.lims.reagent.domain.LimsCultureMedia;
import com.wei.lims.reagent.mapper.LimsCultureMediaMapper;
import com.wei.lims.reagent.service.ILimsCultureMediaService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsCultureMediaServiceImpl implements ILimsCultureMediaService {

    @Autowired
    private LimsCultureMediaMapper limsCultureMediaMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsCultureMedia selectByMediaId(Long mediaId) {
        return limsCultureMediaMapper.selectByMediaId(mediaId);
    }

    @Override
    public List<LimsCultureMedia> selectLimsCultureMediaList(LimsCultureMedia media) {
        return limsCultureMediaMapper.selectLimsCultureMediaList(media);
    }

    @Override
    @Transactional
    public int insertLimsCultureMedia(LimsCultureMedia media) {
        int rows = limsCultureMediaMapper.insertLimsCultureMedia(media);
        limsAuditTrailService.recordFieldChanges("lims_culture_media", media.getMediaId(),
                "INSERT", null, media, "新建培养基记录");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsCultureMedia(LimsCultureMedia media) {
        LimsCultureMedia oldMedia = limsCultureMediaMapper.selectByMediaId(media.getMediaId());
        int rows = limsCultureMediaMapper.updateLimsCultureMedia(media);
        limsAuditTrailService.recordFieldChanges("lims_culture_media", media.getMediaId(),
                "UPDATE", oldMedia, media, "更新培养基记录");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsCultureMediaByIds(Long[] mediaIds) {
        for (Long mediaId : mediaIds) {
            LimsCultureMedia oldMedia = limsCultureMediaMapper.selectByMediaId(mediaId);
            limsAuditTrailService.recordFieldChanges("lims_culture_media", mediaId,
                    "DELETE", oldMedia, null, "删除培养基记录");
        }
        return limsCultureMediaMapper.deleteLimsCultureMediaByIds(mediaIds);
    }
}
