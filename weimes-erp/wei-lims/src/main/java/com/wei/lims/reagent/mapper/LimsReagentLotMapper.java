package com.wei.lims.reagent.mapper;

import java.util.List;
import com.wei.lims.reagent.domain.LimsReagentLot;

public interface LimsReagentLotMapper {

    public LimsReagentLot selectByLotId(Long lotId);

    public List<LimsReagentLot> selectByReagentId(Long reagentId);

    public List<LimsReagentLot> selectLimsReagentLotList(LimsReagentLot lot);

    public int insertLimsReagentLot(LimsReagentLot lot);

    public int updateLimsReagentLot(LimsReagentLot lot);

    public int deleteLimsReagentLotByIds(Long[] lotIds);
}
