package com.wei.lims.capa.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.capa.domain.LimsCapa;
import com.wei.lims.capa.mapper.LimsCapaMapper;
import com.wei.lims.capa.service.ILimsCapaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsCapaServiceImpl implements ILimsCapaService {

    @Autowired
    private LimsCapaMapper limsCapaMapper;

    @Override
    public LimsCapa selectByCapaId(Long capaId) {
        return limsCapaMapper.selectByCapaId(capaId);
    }

    @Override
    public List<LimsCapa> selectLimsCapaList(LimsCapa capa) {
        return limsCapaMapper.selectLimsCapaList(capa);
    }

    @Override
    public int insertLimsCapa(LimsCapa capa) {
        capa.setCreateTime(DateUtils.getNowDate());
        return limsCapaMapper.insertLimsCapa(capa);
    }

    @Override
    public int updateLimsCapa(LimsCapa capa) {
        capa.setUpdateTime(DateUtils.getNowDate());
        return limsCapaMapper.updateLimsCapa(capa);
    }

    @Override
    public int deleteLimsCapaByIds(Long[] capaIds) {
        return limsCapaMapper.deleteLimsCapaByIds(capaIds);
    }

    @Override
    public int submitCapa(Long capaId) {
        LimsCapa capa = limsCapaMapper.selectByCapaId(capaId);
        capa.setStatus("SUBMITTED");
        return limsCapaMapper.updateLimsCapa(capa);
    }

    @Override
    public int approveCapa(Long capaId) {
        LimsCapa capa = limsCapaMapper.selectByCapaId(capaId);
        capa.setStatus("APPROVED");
        return limsCapaMapper.updateLimsCapa(capa);
    }

    @Override
    public int closeCapa(Long capaId) {
        LimsCapa capa = limsCapaMapper.selectByCapaId(capaId);
        capa.setStatus("CLOSED");
        return limsCapaMapper.updateLimsCapa(capa);
    }
}
