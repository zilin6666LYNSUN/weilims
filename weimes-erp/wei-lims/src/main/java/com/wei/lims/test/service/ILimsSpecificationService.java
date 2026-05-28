package com.wei.lims.test.service;

import java.util.List;
import com.wei.lims.test.domain.LimsSpecification;
import com.wei.lims.test.domain.LimsSpecificationLine;

/**
 * 质量标准Service接口
 */
public interface ILimsSpecificationService {

    public LimsSpecification selectLimsSpecificationBySpecificationId(Long specificationId);

    public List<LimsSpecification> selectLimsSpecificationList(LimsSpecification specification);

    public int insertLimsSpecification(LimsSpecification specification);

    public int updateLimsSpecification(LimsSpecification specification);

    public int deleteLimsSpecificationBySpecificationIds(Long[] specificationIds);

    public String checkSpecificationCodeUnique(LimsSpecification specification);

    /** 标准行操作 */
    public List<LimsSpecificationLine> selectLinesBySpecificationId(Long specificationId);

    public int insertSpecificationLine(LimsSpecificationLine line);

    public int updateSpecificationLine(LimsSpecificationLine line);

    public int deleteSpecificationLineByLineIds(Long[] lineIds);
}
