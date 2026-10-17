import request from '@/utils/request'

// 流程定义
export function listProcessDefinition(query) {
  return request({ url: '/lims/process/definition/list', method: 'get', params: query })
}
export function getProcessDefinition(procDefId) {
  return request({ url: '/lims/process/definition/' + procDefId, method: 'get' })
}
export function addProcessDefinition(data) {
  return request({ url: '/lims/process/definition', method: 'post', data: data })
}
export function updateProcessDefinition(data) {
  return request({ url: '/lims/process/definition', method: 'put', data: data })
}
export function delProcessDefinition(procDefIds) {
  return request({ url: '/lims/process/definition/' + procDefIds, method: 'delete' })
}
export function activateProcessDefinition(procDefId) {
  return request({ url: '/lims/process/definition/' + procDefId + '/activate', method: 'put' })
}
export function deactivateProcessDefinition(procDefId) {
  return request({ url: '/lims/process/definition/' + procDefId + '/deactivate', method: 'put' })
}

// 流程实例
export function listProcessInstance(query) {
  return request({ url: '/lims/process/instance/list', method: 'get', params: query })
}
export function getProcessInstance(procInstId) {
  return request({ url: '/lims/process/instance/' + procInstId, method: 'get' })
}
export function startProcessInstance(data) {
  return request({ url: '/lims/process/instance/start', method: 'post', data: data })
}
export function completeProcessTask(data) {
  return request({ url: '/lims/process/instance/complete', method: 'post', data: data })
}
export function terminateProcessInstance(data) {
  return request({ url: '/lims/process/instance/terminate', method: 'post', data: data })
}
export function listTodo() {
  return request({ url: '/lims/process/instance/todo', method: 'get' })
}

// 审批历史
export function listApprovalHistory(procInstId) {
  return request({ url: '/lims/process/approval/history/' + procInstId, method: 'get' })
}
export function listApprovalHistoryByBusiness(businessType, businessId) {
  return request({ url: '/lims/process/approval/business/' + businessType + '/' + businessId, method: 'get' })
}
