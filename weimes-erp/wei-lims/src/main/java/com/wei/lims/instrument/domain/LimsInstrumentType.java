package com.wei.lims.instrument.domain;

/**
 * 仪器类型对象 lims_instrument_type
 */
public class LimsInstrumentType {
    private static final long serialVersionUID = 1L;

    private Long typeId;
    private String typeCode;
    private String typeName;
    private String category;
    private Long parentId;

    public Long getTypeId() { return typeId; }
    public void setTypeId(Long typeId) { this.typeId = typeId; }
    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
