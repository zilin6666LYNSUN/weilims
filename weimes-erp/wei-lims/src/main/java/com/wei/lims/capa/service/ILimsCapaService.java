package com.wei.lims.capa.service;

import java.util.List;
import com.wei.lims.capa.domain.LimsCapa;

public interface ILimsCapaService {
    public LimsCapa selectByCapaId(Long capaId);
    public List<LimsCapa> selectLimsCapaList(LimsCapa capa);
    public int insertLimsCapa(LimsCapa capa);
    public int updateLimsCapa(LimsCapa capa);
    public int deleteLimsCapaByIds(Long[] capaIds);
    public int submitCapa(Long capaId);
    public int approveCapa(Long capaId);
    public int closeCapa(Long capaId);
}
