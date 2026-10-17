package com.wei.lims.entrust.service;

import java.util.List;
import com.wei.lims.entrust.domain.LimsEntrust;

public interface ILimsEntrustService {
    public LimsEntrust selectLimsEntrustByEntrustId(Long entrustId);
    public List<LimsEntrust> selectLimsEntrustList(LimsEntrust LimsEntrust);
    public int insertLimsEntrust(LimsEntrust limsentrust);
    public int updateLimsEntrust(LimsEntrust limsentrust);
    public int deleteLimsEntrustByEntrustId(Long[] entrustIds);

    public int submitLimsEntrust(Long entrustId);

    public int reviewLimsEntrust(LimsEntrust entrust);

    public Long registerSample(Long entrustId);
}