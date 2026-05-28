package com.wei.lims.change.service;

import java.util.List;
import com.wei.lims.change.domain.LimsChangeControl;

public interface ILimsChangeControlService {
    public LimsChangeControl selectByChangeId(Long changeId);
    public List<LimsChangeControl> selectLimsChangeControlList(LimsChangeControl changeControl);
    public int insertLimsChangeControl(LimsChangeControl changeControl);
    public int updateLimsChangeControl(LimsChangeControl changeControl);
    public int deleteLimsChangeControlByIds(Long[] changeIds);
    public int submitChange(Long changeId);
    public int approveChange(Long changeId);
    public int implementChange(Long changeId);
    public int completeChange(Long changeId);
}
