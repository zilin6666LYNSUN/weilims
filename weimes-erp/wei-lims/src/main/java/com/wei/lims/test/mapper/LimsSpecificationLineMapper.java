package com.wei.lims.test.mapper;

import java.util.List;
import com.wei.lims.test.domain.LimsSpecificationLine;

public interface LimsSpecificationLineMapper {

    public List<LimsSpecificationLine> selectBySpecificationId(Long specificationId);

    public LimsSpecificationLine selectByLineId(Long lineId);

    public List<LimsSpecificationLine> selectLimsSpecificationLineList(LimsSpecificationLine line);

    public int insertLimsSpecificationLine(LimsSpecificationLine line);

    public int updateLimsSpecificationLine(LimsSpecificationLine line);

    public int deleteLimsSpecificationLineByLineIds(Long[] lineIds);

    public int deleteBySpecificationId(Long specificationId);
}
