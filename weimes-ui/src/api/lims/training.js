import request from '@/utils/request'

// 人员档案
export function listStaff(query) {
  return request({ url: '/lims/staff/list', method: 'get', params: query })
}
export function getStaff(staffId) {
  return request({ url: '/lims/staff/' + staffId, method: 'get' })
}
export function addStaff(data) {
  return request({ url: '/lims/staff', method: 'post', data: data })
}
export function updateStaff(data) {
  return request({ url: '/lims/staff', method: 'put', data: data })
}
export function delStaff(staffIds) {
  return request({ url: '/lims/staff/' + staffIds, method: 'delete' })
}

// 检测授权
export function listStaffTestAuthorization(query) {
  return request({ url: '/lims/staff/authorization/test/list', method: 'get', params: query })
}
export function getStaffTestAuthorization(authId) {
  return request({ url: '/lims/staff/authorization/test/' + authId, method: 'get' })
}
export function addStaffTestAuthorization(data) {
  return request({ url: '/lims/staff/authorization/test', method: 'post', data: data })
}
export function updateStaffTestAuthorization(data) {
  return request({ url: '/lims/staff/authorization/test', method: 'put', data: data })
}
export function delStaffTestAuthorization(authIds) {
  return request({ url: '/lims/staff/authorization/test/' + authIds, method: 'delete' })
}

// 仪器授权
export function listStaffInstrumentAuthorization(query) {
  return request({ url: '/lims/staff/authorization/instrument/list', method: 'get', params: query })
}
export function getStaffInstrumentAuthorization(authId) {
  return request({ url: '/lims/staff/authorization/instrument/' + authId, method: 'get' })
}
export function addStaffInstrumentAuthorization(data) {
  return request({ url: '/lims/staff/authorization/instrument', method: 'post', data: data })
}
export function updateStaffInstrumentAuthorization(data) {
  return request({ url: '/lims/staff/authorization/instrument', method: 'put', data: data })
}
export function delStaffInstrumentAuthorization(authIds) {
  return request({ url: '/lims/staff/authorization/instrument/' + authIds, method: 'delete' })
}

// 培训计划
export function listTrainingPlan(query) {
  return request({ url: '/lims/training/plan/list', method: 'get', params: query })
}
export function getTrainingPlan(planId) {
  return request({ url: '/lims/training/plan/' + planId, method: 'get' })
}
export function addTrainingPlan(data) {
  return request({ url: '/lims/training/plan', method: 'post', data: data })
}
export function updateTrainingPlan(data) {
  return request({ url: '/lims/training/plan', method: 'put', data: data })
}
export function delTrainingPlan(planIds) {
  return request({ url: '/lims/training/plan/' + planIds, method: 'delete' })
}

// 培训记录
export function listTrainingRecord(query) {
  return request({ url: '/lims/training/record/list', method: 'get', params: query })
}
export function getTrainingRecord(trainingId) {
  return request({ url: '/lims/training/record/' + trainingId, method: 'get' })
}
export function addTrainingRecord(data) {
  return request({ url: '/lims/training/record', method: 'post', data: data })
}
export function updateTrainingRecord(data) {
  return request({ url: '/lims/training/record', method: 'put', data: data })
}
export function delTrainingRecord(trainingIds) {
  return request({ url: '/lims/training/record/' + trainingIds, method: 'delete' })
}

// 培训参与人员
export function listTrainingParticipant(query) {
  return request({ url: '/lims/training/participant/list', method: 'get', params: query })
}
export function getTrainingParticipant(participantId) {
  return request({ url: '/lims/training/participant/' + participantId, method: 'get' })
}
export function addTrainingParticipant(data) {
  return request({ url: '/lims/training/participant', method: 'post', data: data })
}
export function updateTrainingParticipant(data) {
  return request({ url: '/lims/training/participant', method: 'put', data: data })
}
export function delTrainingParticipant(participantIds) {
  return request({ url: '/lims/training/participant/' + participantIds, method: 'delete' })
}

// 上岗资质
export function listQualification(query) {
  return request({ url: '/lims/qualification/list', method: 'get', params: query })
}
export function getQualification(qualificationId) {
  return request({ url: '/lims/qualification/' + qualificationId, method: 'get' })
}
export function addQualification(data) {
  return request({ url: '/lims/qualification', method: 'post', data: data })
}
export function updateQualification(data) {
  return request({ url: '/lims/qualification', method: 'put', data: data })
}
export function delQualification(qualificationIds) {
  return request({ url: '/lims/qualification/' + qualificationIds, method: 'delete' })
}

// 培训参与人员(按培训记录)
export function listParticipantByTraining(trainingId) {
  return request({ url: '/lims/training/participant/list/' + trainingId, method: 'get' })
}
// 上岗资质到期提醒(30天内)
export function listExpiringQualification() {
  return request({ url: '/lims/qualification/expiring', method: 'get' })
}

