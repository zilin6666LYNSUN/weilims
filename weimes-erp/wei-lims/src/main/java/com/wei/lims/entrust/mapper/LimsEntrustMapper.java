package com.wei.lims.entrust.mapper;

import java.util.List;
import com.wei.lims.entrust.domain.LimsEntrust;

public interface LimsEntrustMapper {
    public LimsEntrust selectLimsEntrustByEntrustId(Long entrustId);
    public List<LimsEntrust> selectLimsEntrustList(LimsEntrust LimsEntrust);
    public int insertLimsEntrust(LimsEntrust limsentrust);
    public int updateLimsEntrust(LimsEntrust limsentrust);
    public int deleteLimsEntrustByEntrustId(Long[] entrustIds);
}