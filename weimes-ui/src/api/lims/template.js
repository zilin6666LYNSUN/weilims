import request from '@/utils/request'

// 查询报告模板列表
export function listTemplate(query) {
  return request({
    url: '/lims/report/template/list',
    method: 'get',
    params: query
  })
}

// 查询报告模板详情
export function getTemplate(templateId) {
  return request({
    url: '/lims/report/template/' + templateId,
    method: 'get'
  })
}

// 新增报告模板
export function addTemplate(data) {
  return request({
    url: '/lims/report/template',
    method: 'post',
    data: data
  })
}

// 修改报告模板
export function updateTemplate(data) {
  return request({
    url: '/lims/report/template',
    method: 'put',
    data: data
  })
}

// 删除报告模板
export function delTemplate(templateIds) {
  return request({
    url: '/lims/report/template/' + templateIds,
    method: 'delete'
  })
}
