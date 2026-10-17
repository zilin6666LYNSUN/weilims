package com.wei.lims.capa.mapper;

import java.util.List;
import com.wei.lims.capa.domain.LimsCapaTask;

public interface LimsCapaTaskMapper {
    public LimsCapaTask selectByTaskId(Long taskId);
    public List<LimsCapaTask> selectByCapaId(Long capaId);
    public List<LimsCapaTask> selectLimsCapaTaskList(LimsCapaTask task);
    public int insertLimsCapaTask(LimsCapaTask task);
    public int updateLimsCapaTask(LimsCapaTask task);
    public int deleteLimsCapaTaskByIds(Long[] taskIds);
}
