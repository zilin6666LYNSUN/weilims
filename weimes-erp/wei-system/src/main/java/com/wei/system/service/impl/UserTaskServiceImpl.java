package com.wei.system.service.impl;

import com.wei.system.domain.UserTask;
import com.wei.system.mapper.UserTaskMapper;
import com.wei.system.service.IUserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTaskServiceImpl implements IUserTaskService {

    @Autowired
    private UserTaskMapper userTaskMapper;

    @Override
    public List<UserTask> listTodoList(String userName) {
        return userTaskMapper.listTodoList(userName);
    }

    @Override
    public List<UserTask> listFinishedList(String userName) {
        return userTaskMapper.listFinishedList(userName);
    }
}
