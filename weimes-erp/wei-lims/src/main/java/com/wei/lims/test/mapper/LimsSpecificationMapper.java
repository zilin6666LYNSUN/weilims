package com.wei.lims.test.mapper;

import java.util.List;
import com.wei.lims.test.domain.LimsSpecification;

public interface LimsSpecificationMapper {

    public LimsSpecification selectLimsSpecificationBySpecificationId(Long specificationId);

    public LimsSpecification selectLimsSpecificationBySpecificationCode(String specificationCode);

    public List<LimsSpecification> selectLimsSpecificationList(LimsSpecification specification);

    public int insertLimsSpecification(LimsSpecification specification);

    public int updateLimsSpecification(LimsSpecification specification);

    public int deleteLimsSpecificationBySpecificationIds(Long[] specificationIds);
}
