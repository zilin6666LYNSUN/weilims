import request from '@/utils/request'

// 查询检验报告列表
export function listReport(query) {
  return request({
    url: '/lims/report/test/list',
    method: 'get',
    params: query
  })
}

// 查询检验报告详情
export function getReport(reportId) {
  return request({
    url: '/lims/report/test/' + reportId,
    method: 'get'
  })
}

// 新增检验报告
export function addReport(data) {
  return request({
    url: '/lims/report/test',
    method: 'post',
    data: data
  })
}

// 从检验订单创建报告
export function createFromOrder(testOrderId, templateId, reportType) {
  return request({
    url: '/lims/report/test/createFromOrder',
    method: 'post',
    params: { testOrderId, templateId, reportType }
  })
}

// 修改检验报告
export function updateReport(data) {
  return request({
    url: '/lims/report/test',
    method: 'put',
    data: data
  })
}

// 删除检验报告
export function delReport(reportIds) {
  return request({
    url: '/lims/report/test/' + reportIds,
    method: 'delete'
  })
}

// 生成报告
export function generateReport(reportId) {
  return request({
    url: '/lims/report/test/generate/' + reportId,
    method: 'put'
  })
}

// 报告复核
export function reviewReport(reportId, reviewResult, comment) {
  return request({
    url: '/lims/report/test/review/' + reportId,
    method: 'put',
    params: { reviewResult, comment }
  })
}

// 报告批准
export function approveReport(reportId, approveResult, comment) {
  return request({
    url: '/lims/report/test/approve/' + reportId,
    method: 'put',
    params: { approveResult, comment }
  })
}

// 报告放行
export function releaseReport(reportId) {
  return request({
    url: '/lims/report/test/release/' + reportId,
    method: 'put'
  })
}

// 作废报告
export function voidReport(reportId, voidReason) {
  return request({
    url: '/lims/report/test/void/' + reportId,
    method: 'put',
    params: { voidReason }
  })
}

// 打印报告
export function printReport(reportId) {
  return request({
    url: '/lims/report/test/print/' + reportId,
    method: 'put'
  })
}

// 查询报告签章记录
export function getReportSignatures(reportId) {
  return request({
    url: '/lims/report/test/signatures/' + reportId,
    method: 'get'
  })
}
