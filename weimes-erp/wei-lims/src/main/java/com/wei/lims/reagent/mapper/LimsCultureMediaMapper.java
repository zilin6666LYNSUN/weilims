package com.wei.lims.reagent.mapper;

import java.util.List;
import com.wei.lims.reagent.domain.LimsCultureMedia;

public interface LimsCultureMediaMapper {

    public LimsCultureMedia selectByMediaId(Long mediaId);

    public List<LimsCultureMedia> selectLimsCultureMediaList(LimsCultureMedia media);

    public int insertLimsCultureMedia(LimsCultureMedia media);

    public int updateLimsCultureMedia(LimsCultureMedia media);

    public int deleteLimsCultureMediaByIds(Long[] mediaIds);
}
