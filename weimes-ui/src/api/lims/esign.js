import request from '@/utils/request'

// 创建电子签名
export function createSignature(data) {
  return request({
    url: '/lims/esign',
    method: 'post',
    data: data
  })
}

// 验证电子签名
export function verifySignature(signatureId, password) {
  return request({
    url: '/lims/esign/verify/' + signatureId,
    method: 'post',
    params: { password }
  })
}

// 查询签名列表
export function listSignature(query) {
  return request({
    url: '/lims/esign/list',
    method: 'get',
    params: query
  })
}

// 查询业务记录的签名
export function getSignatures(recordType, recordId) {
  return request({
    url: '/lims/esign/records/' + recordType + '/' + recordId,
    method: 'get'
  })
}

// 查询审计追踪列表
export function listAuditTrail(query) {
  return request({
    url: '/lims/audit-trail/list',
    method: 'get',
    params: query
  })
}

// 查询指定记录的变更历史
export function getAuditHistory(tableName, recordId) {
  return request({
    url: '/lims/audit-trail/history/' + tableName + '/' + recordId,
    method: 'get'
  })
}
