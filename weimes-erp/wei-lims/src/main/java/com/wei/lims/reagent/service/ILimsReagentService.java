package com.wei.lims.reagent.service;

import java.util.List;
import com.wei.lims.reagent.domain.LimsReagent;

public interface ILimsReagentService {
    public LimsReagent selectByReagentId(Long reagentId);
    public List<LimsReagent> selectLimsReagentList(LimsReagent reagent);
    public int insertLimsReagent(LimsReagent reagent);
    public int updateLimsReagent(LimsReagent reagent);
    public int deleteLimsReagentByIds(Long[] reagentIds);
}
