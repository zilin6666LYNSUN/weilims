package com.wei.lims.report.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import com.wei.common.utils.DateUtils;
import com.wei.common.utils.SecurityUtils;
import com.wei.lims.report.domain.LimsInstrumentDataAcquisition;
import com.wei.lims.report.mapper.LimsInstrumentDataAcquisitionMapper;
import com.wei.lims.report.service.ILimsInstrumentDataAcquisitionService;
import com.wei.lims.test.domain.LimsTestResult;
import com.wei.lims.test.mapper.LimsTestResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 仪器数据采集Service实现
 * 当前为模拟实现，实际需对接仪器接口（RS232/LAN/OPC等）
 */
@Service
public class LimsInstrumentDataAcquisitionServiceImpl implements ILimsInstrumentDataAcquisitionService {

    private static final Logger log = LoggerFactory.getLogger(LimsInstrumentDataAcquisitionServiceImpl.class);

    @Autowired
    private LimsInstrumentDataAcquisitionMapper acquisitionMapper;

    @Autowired
    private LimsTestResultMapper testResultMapper;

    @Override
    public LimsInstrumentDataAcquisition selectAcquisitionById(Long acquisitionId) {
        return acquisitionMapper.selectAcquisitionById(acquisitionId);
    }

    @Override
    public LimsInstrumentDataAcquisition selectAcquisitionByInstrumentId(Long instrumentId) {
        return acquisitionMapper.selectAcquisitionByInstrumentId(instrumentId);
    }

    @Override
    public List<LimsInstrumentDataAcquisition> selectAcquisitionList(LimsInstrumentDataAcquisition acquisition) {
        return acquisitionMapper.selectAcquisitionList(acquisition);
    }

    @Override
    public int insertAcquisition(LimsInstrumentDataAcquisition acquisition) {
        acquisition.setCreateTime(DateUtils.getNowDate());
        return acquisitionMapper.insertAcquisition(acquisition);
    }

    @Override
    public int updateAcquisition(LimsInstrumentDataAcquisition acquisition) {
        acquisition.setUpdateTime(DateUtils.getNowDate());
        return acquisitionMapper.updateAcquisition(acquisition);
    }

    @Override
    public int deleteAcquisitionByIds(Long[] acquisitionIds) {
        return acquisitionMapper.deleteAcquisitionByIds(acquisitionIds);
    }

    /**
     * 模拟从仪器采集数据并填充到检验结果
     * 实际实现需根据采集配置中的接口类型、连接参数、解析规则等进行真实数据采集
     */
    @Override
    @Transactional
    public int importInstrumentData(Long instrumentId, Long testOrderId) {
        LimsInstrumentDataAcquisition config = acquisitionMapper.selectAcquisitionByInstrumentId(instrumentId);
        if (config == null) {
            log.warn("仪器{}未配置数据采集", instrumentId);
            return 0;
        }

        // 获取该检验订单中关联此仪器的检验结果
        List<LimsTestResult> results = testResultMapper.selectLimsTestResultByTestOrderId(testOrderId);
        int importedCount = 0;

        for (LimsTestResult result : results) {
            if (instrumentId.equals(result.getInstrumentId())) {
                // 模拟仪器数据采集：在标准范围内生成随机值
                if (result.getLowerLimit() != null && result.getUpperLimit() != null) {
                    BigDecimal range = result.getUpperLimit().subtract(result.getLowerLimit());
                    BigDecimal randomOffset = range.multiply(new BigDecimal(new Random().nextDouble()));
                    BigDecimal simulatedValue = result.getLowerLimit().add(randomOffset)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);

                    result.setInstrumentValue(simulatedValue.toPlainString());
                    result.setOriginalValue(simulatedValue.toPlainString());
                    result.setReportedValue(simulatedValue.toPlainString());

                    testResultMapper.updateLimsTestResult(result);
                    importedCount++;
                    log.info("仪器{}采集数据: 检测项={}, 值={}", instrumentId, result.getTestItemName(), simulatedValue);
                }
            }
        }

        log.info("仪器{}数据采集完成，导入{}条数据", instrumentId, importedCount);
        return importedCount;
    }
}
