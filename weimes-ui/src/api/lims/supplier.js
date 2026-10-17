import request from '@/utils/request'

// 供应商档案
export function listSupplier(query) {
  return request({ url: '/lims/supplier/list', method: 'get', params: query })
}
export function getSupplier(vendorId) {
  return request({ url: '/lims/supplier/' + vendorId, method: 'get' })
}
export function addSupplier(data) {
  return request({ url: '/lims/supplier', method: 'post', data: data })
}
export function updateSupplier(data) {
  return request({ url: '/lims/supplier', method: 'put', data: data })
}
export function delSupplier(vendorIds) {
  return request({ url: '/lims/supplier/' + vendorIds, method: 'delete' })
}

// 供应商资质
export function listSupplierQualification(query) {
  return request({ url: '/lims/supplier/qualification/list', method: 'get', params: query })
}
export function getSupplierQualification(qualificationId) {
  return request({ url: '/lims/supplier/qualification/' + qualificationId, method: 'get' })
}
export function addSupplierQualification(data) {
  return request({ url: '/lims/supplier/qualification', method: 'post', data: data })
}
export function updateSupplierQualification(data) {
  return request({ url: '/lims/supplier/qualification', method: 'put', data: data })
}
export function delSupplierQualification(qualificationIds) {
  return request({ url: '/lims/supplier/qualification/' + qualificationIds, method: 'delete' })
}

// 供应商审计
export function listSupplierAudit(query) {
  return request({ url: '/lims/supplier/audit/list', method: 'get', params: query })
}
export function getSupplierAudit(auditId) {
  return request({ url: '/lims/supplier/audit/' + auditId, method: 'get' })
}
export function addSupplierAudit(data) {
  return request({ url: '/lims/supplier/audit', method: 'post', data: data })
}
export function updateSupplierAudit(data) {
  return request({ url: '/lims/supplier/audit', method: 'put', data: data })
}
export function delSupplierAudit(auditIds) {
  return request({ url: '/lims/supplier/audit/' + auditIds, method: 'delete' })
}

// 供应商审批
export function listSupplierApproval(query) {
  return request({ url: '/lims/supplier/approval/list', method: 'get', params: query })
}
export function getSupplierApproval(approvalId) {
  return request({ url: '/lims/supplier/approval/' + approvalId, method: 'get' })
}
export function addSupplierApproval(data) {
  return request({ url: '/lims/supplier/approval', method: 'post', data: data })
}
export function updateSupplierApproval(data) {
  return request({ url: '/lims/supplier/approval', method: 'put', data: data })
}
export function delSupplierApproval(approvalIds) {
  return request({ url: '/lims/supplier/approval/' + approvalIds, method: 'delete' })
}

// 采购记录
export function listSupplierPurchase(query) {
  return request({ url: '/lims/supplier/purchase/list', method: 'get', params: query })
}
export function getSupplierPurchase(purchaseId) {
  return request({ url: '/lims/supplier/purchase/' + purchaseId, method: 'get' })
}
export function addSupplierPurchase(data) {
  return request({ url: '/lims/supplier/purchase', method: 'post', data: data })
}
export function updateSupplierPurchase(data) {
  return request({ url: '/lims/supplier/purchase', method: 'put', data: data })
}
export function delSupplierPurchase(purchaseIds) {
  return request({ url: '/lims/supplier/purchase/' + purchaseIds, method: 'delete' })
}

