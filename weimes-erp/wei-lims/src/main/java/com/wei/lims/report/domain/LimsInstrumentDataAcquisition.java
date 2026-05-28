package com.wei.lims.report.domain;

import com.wei.common.core.domain.BaseEntity;

/**
 * 仪器数据采集配置对象 lims_instrument_data_acquisition
 */
public class LimsInstrumentDataAcquisition extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long acquisitionId;
    private Long instrumentId;
    private String instrumentCode;

    /** 接口类型: RS232/LAN/USB/FILE/OPC/MQTT */
    private String interfaceType;

    /** 连接参数(JSON) */
    private String connectionConfig;

    /** 数据协议: ASCII/BINARY/HL7/ASTM/JSON */
    private String dataProtocol;

    /** 数据解析规则(JSON) */
    private String parsingRules;

    /** 采集字段映射(JSON) */
    private String fieldMappings;

    private Integer autoImport;
    private Integer pollInterval;
    private String status;

    public Long getAcquisitionId() { return acquisitionId; }
    public void setAcquisitionId(Long acquisitionId) { this.acquisitionId = acquisitionId; }
    public Long getInstrumentId() { return instrumentId; }
    public void setInstrumentId(Long instrumentId) { this.instrumentId = instrumentId; }
    public String getInstrumentCode() { return instrumentCode; }
    public void setInstrumentCode(String instrumentCode) { this.instrumentCode = instrumentCode; }
    public String getInterfaceType() { return interfaceType; }
    public void setInterfaceType(String interfaceType) { this.interfaceType = interfaceType; }
    public String getConnectionConfig() { return connectionConfig; }
    public void setConnectionConfig(String connectionConfig) { this.connectionConfig = connectionConfig; }
    public String getDataProtocol() { return dataProtocol; }
    public void setDataProtocol(String dataProtocol) { this.dataProtocol = dataProtocol; }
    public String getParsingRules() { return parsingRules; }
    public void setParsingRules(String parsingRules) { this.parsingRules = parsingRules; }
    public String getFieldMappings() { return fieldMappings; }
    public void setFieldMappings(String fieldMappings) { this.fieldMappings = fieldMappings; }
    public Integer getAutoImport() { return autoImport; }
    public void setAutoImport(Integer autoImport) { this.autoImport = autoImport; }
    public Integer getPollInterval() { return pollInterval; }
    public void setPollInterval(Integer pollInterval) { this.pollInterval = pollInterval; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
