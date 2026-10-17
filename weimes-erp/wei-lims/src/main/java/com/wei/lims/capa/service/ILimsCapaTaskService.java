package com.wei.lims.capa.service;

import java.util.List;
import com.wei.lims.capa.domain.LimsCapaTask;

public interface ILimsCapaTaskService {
    public LimsCapaTask selectByTaskId(Long taskId);
    public List<LimsCapaTask> selectByCapaId(Long capaId);
    public List<LimsCapaTask> selectLimsCapaTaskList(LimsCapaTask task);
    public int insertLimsCapaTask(LimsCapaTask task);
    public int updateLimsCapaTask(LimsCapaTask task);
    public int deleteLimsCapaTaskByIds(Long[] taskIds);

    /** 完成任务 */
    public int completeTask(Long taskId);
}
