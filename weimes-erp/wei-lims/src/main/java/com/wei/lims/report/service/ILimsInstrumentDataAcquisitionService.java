package com.wei.lims.report.service;

import java.util.List;
import com.wei.lims.report.domain.LimsInstrumentDataAcquisition;

/**
 * 仪器数据采集配置Service接口
 */
public interface ILimsInstrumentDataAcquisitionService {

    public LimsInstrumentDataAcquisition selectAcquisitionById(Long acquisitionId);

    public LimsInstrumentDataAcquisition selectAcquisitionByInstrumentId(Long instrumentId);

    public List<LimsInstrumentDataAcquisition> selectAcquisitionList(LimsInstrumentDataAcquisition acquisition);

    public int insertAcquisition(LimsInstrumentDataAcquisition acquisition);

    public int updateAcquisition(LimsInstrumentDataAcquisition acquisition);

    public int deleteAcquisitionByIds(Long[] acquisitionIds);

    /**
     * 模拟从仪器采集数据并填充到检验结果
     */
    public int importInstrumentData(Long instrumentId, Long testOrderId);
}
