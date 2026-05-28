package com.wei.lims.report.mapper;

import java.util.List;
import com.wei.lims.report.domain.LimsInstrumentDataAcquisition;

/**
 * 仪器数据采集配置Mapper接口
 */
public interface LimsInstrumentDataAcquisitionMapper {

    public LimsInstrumentDataAcquisition selectAcquisitionById(Long acquisitionId);

    public LimsInstrumentDataAcquisition selectAcquisitionByInstrumentId(Long instrumentId);

    public List<LimsInstrumentDataAcquisition> selectAcquisitionList(LimsInstrumentDataAcquisition acquisition);

    public int insertAcquisition(LimsInstrumentDataAcquisition acquisition);

    public int updateAcquisition(LimsInstrumentDataAcquisition acquisition);

    public int deleteAcquisitionByIds(Long[] acquisitionIds);
}
