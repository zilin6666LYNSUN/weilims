package com.wei.system.service;

import com.wei.system.domain.UserTask;

import java.util.List;

public interface IUserTaskService {

    public List<UserTask> listTodoList(String usesrName);

    public List<UserTask> listFinishedList(String usesrName);

}
