package com.wei.lims.capa.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.capa.domain.LimsCapa;
import com.wei.lims.capa.domain.LimsCapaTask;
import com.wei.lims.capa.mapper.LimsCapaMapper;
import com.wei.lims.capa.mapper.LimsCapaTaskMapper;
import com.wei.lims.capa.service.ILimsCapaService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsCapaServiceImpl implements ILimsCapaService {

    @Autowired
    private LimsCapaMapper limsCapaMapper;

    @Autowired
    private LimsCapaTaskMapper limsCapaTaskMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsCapa selectByCapaId(Long capaId) {
        return limsCapaMapper.selectByCapaId(capaId);
    }

    @Override
    public List<LimsCapa> selectLimsCapaList(LimsCapa capa) {
        return limsCapaMapper.selectLimsCapaList(capa);
    }

    @Override
    @Transactional
    public int insertLimsCapa(LimsCapa capa) {
        capa.setCreateTime(DateUtils.getNowDate());
        int rows = limsCapaMapper.insertLimsCapa(capa);
        limsAuditTrailService.recordFieldChanges("lims_capa", capa.getCapaId(),
                "INSERT", null, capa, "新建CAPA");
        // 自动创建默认CAPA执行任务（待责任人确认并完成）
        LimsCapaTask task = new LimsCapaTask();
        task.setCapaId(capa.getCapaId());
        task.setTaskDescription("CAPA整改执行：" + (capa.getDescription() == null ? "" : capa.getDescription()));
        task.setStatus("PENDING");
        task.setDueDate(DateUtils.getNowDate());
        limsCapaTaskMapper.insertLimsCapaTask(task);
        limsAuditTrailService.recordFieldChanges("lims_capa_task", task.getTaskId(),
                "INSERT", null, task, "新建CAPA自动触发执行任务");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsCapa(LimsCapa capa) {
        LimsCapa oldCapa = limsCapaMapper.selectByCapaId(capa.getCapaId());
        capa.setUpdateTime(DateUtils.getNowDate());
        int rows = limsCapaMapper.updateLimsCapa(capa);
        limsAuditTrailService.recordFieldChanges("lims_capa", capa.getCapaId(),
                "UPDATE", oldCapa, capa, "更新CAPA");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsCapaByIds(Long[] capaIds) {
        for (Long capaId : capaIds) {
            limsAuditTrailService.recordFieldChanges("lims_capa", capaId,
                    "DELETE", null, null, "删除CAPA");
        }
        return limsCapaMapper.deleteLimsCapaByIds(capaIds);
    }

    @Override
    @Transactional
    public int submitCapa(Long capaId) {
        LimsCapa oldCapa = limsCapaMapper.selectByCapaId(capaId);
        LimsCapa newCapa = limsCapaMapper.selectByCapaId(capaId);
        newCapa.setStatus("SUBMITTED");
        int rows = limsCapaMapper.updateLimsCapa(newCapa);
        limsAuditTrailService.recordFieldChanges("lims_capa", capaId,
                "UPDATE", oldCapa, newCapa, "提交CAPA");
        return rows;
    }

    @Override
    @Transactional
    public int approveCapa(Long capaId) {
        LimsCapa oldCapa = limsCapaMapper.selectByCapaId(capaId);
        LimsCapa newCapa = limsCapaMapper.selectByCapaId(capaId);
        newCapa.setStatus("APPROVED");
        int rows = limsCapaMapper.updateLimsCapa(newCapa);
        limsAuditTrailService.recordFieldChanges("lims_capa", capaId,
                "UPDATE", oldCapa, newCapa, "审批CAPA");
        return rows;
    }

    @Override
    @Transactional
    public int closeCapa(Long capaId) {
        LimsCapa oldCapa = limsCapaMapper.selectByCapaId(capaId);
        LimsCapa newCapa = limsCapaMapper.selectByCapaId(capaId);
        newCapa.setStatus("CLOSED");
        int rows = limsCapaMapper.updateLimsCapa(newCapa);
        limsAuditTrailService.recordFieldChanges("lims_capa", capaId,
                "UPDATE", oldCapa, newCapa, "关闭CAPA");
        return rows;
    }
}
