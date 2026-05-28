package com.wei.lims.reagent.mapper;

import java.util.List;
import com.wei.lims.reagent.domain.LimsReagent;

public interface LimsReagentMapper {
    public LimsReagent selectByReagentId(Long reagentId);
    public LimsReagent selectByReagentCode(String reagentCode);
    public List<LimsReagent> selectLimsReagentList(LimsReagent reagent);
    public int insertLimsReagent(LimsReagent reagent);
    public int updateLimsReagent(LimsReagent reagent);
    public int deleteLimsReagentByIds(Long[] reagentIds);
}
