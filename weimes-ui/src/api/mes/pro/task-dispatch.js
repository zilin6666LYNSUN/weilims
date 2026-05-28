import request from '@/utils/request'

// 查询生产任务列表
export function listTaskDispatch(query) {
  return request({
    url: '/pro/task-dispatch/list',
    method: 'get',
    params: query
  })
}

// 查询生产任务详细
export function getTaskDispatch(taskId) {
  return request({
    url: '/pro/task-dispatch/' + taskId,
    method: 'get'
  })
}


// 生产派单指派用户单个
export function updateTaskDispatch(query) {
  return request({
    url: '/pro/task-dispatch/addAssignUsers',
    method: 'post',
    data: query // 使用 data 传递参数
  })
}


// 删除生产任务指派人
export function removeAssignUsers(taskId) {
  return request({
    url: '/pro/task-dispatch/removeAssignUsers/' + taskId,
    method: 'put'
  })
}