package com.wei.lims.capa.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.capa.domain.LimsCapaTask;
import com.wei.lims.capa.mapper.LimsCapaTaskMapper;
import com.wei.lims.capa.service.ILimsCapaTaskService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsCapaTaskServiceImpl implements ILimsCapaTaskService {

    @Autowired
    private LimsCapaTaskMapper limsCapaTaskMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsCapaTask selectByTaskId(Long taskId) {
        return limsCapaTaskMapper.selectByTaskId(taskId);
    }

    @Override
    public List<LimsCapaTask> selectByCapaId(Long capaId) {
        return limsCapaTaskMapper.selectByCapaId(capaId);
    }

    @Override
    public List<LimsCapaTask> selectLimsCapaTaskList(LimsCapaTask task) {
        return limsCapaTaskMapper.selectLimsCapaTaskList(task);
    }

    @Override
    @Transactional
    public int insertLimsCapaTask(LimsCapaTask task) {
        int rows = limsCapaTaskMapper.insertLimsCapaTask(task);
        limsAuditTrailService.recordFieldChanges("lims_capa_task", task.getTaskId(),
                "INSERT", null, task, "新建CAPA任务");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsCapaTask(LimsCapaTask task) {
        LimsCapaTask oldTask = limsCapaTaskMapper.selectByTaskId(task.getTaskId());
        int rows = limsCapaTaskMapper.updateLimsCapaTask(task);
        limsAuditTrailService.recordFieldChanges("lims_capa_task", task.getTaskId(),
                "UPDATE", oldTask, task, "更新CAPA任务");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsCapaTaskByIds(Long[] taskIds) {
        for (Long taskId : taskIds) {
            LimsCapaTask oldTask = limsCapaTaskMapper.selectByTaskId(taskId);
            limsAuditTrailService.recordFieldChanges("lims_capa_task", taskId,
                    "DELETE", oldTask, null, "删除CAPA任务");
        }
        return limsCapaTaskMapper.deleteLimsCapaTaskByIds(taskIds);
    }

    @Override
    @Transactional
    public int completeTask(Long taskId) {
        LimsCapaTask oldTask = limsCapaTaskMapper.selectByTaskId(taskId);
        LimsCapaTask upd = new LimsCapaTask();
        upd.setTaskId(taskId);
        upd.setStatus("COMPLETED");
        upd.setCompletionDate(DateUtils.getNowDate());
        int rows = limsCapaTaskMapper.updateLimsCapaTask(upd);
        limsAuditTrailService.recordFieldChanges("lims_capa_task", taskId,
                "UPDATE", oldTask, upd, "完成CAPA任务");
        return rows;
    }
}
