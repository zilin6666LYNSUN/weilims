package com.wei.lims.capa.mapper;

import java.util.List;
import com.wei.lims.capa.domain.LimsCapa;

public interface LimsCapaMapper {
    public LimsCapa selectByCapaId(Long capaId);
    public List<LimsCapa> selectLimsCapaList(LimsCapa capa);
    public int insertLimsCapa(LimsCapa capa);
    public int updateLimsCapa(LimsCapa capa);
    public int deleteLimsCapaByIds(Long[] capaIds);
}
