import request from '@/utils/request'

// 查询检验方法列表
export function listMethod(query) {
  return request({ url: '/lims/test/method/list', method: 'get', params: query })
}

// 查询检验方法详情
export function getMethod(methodId) {
  return request({ url: '/lims/test/method/' + methodId, method: 'get' })
}

// 新增检验方法
export function addMethod(data) {
  return request({ url: '/lims/test/method', method: 'post', data: data })
}

// 修改检验方法
export function updateMethod(data) {
  return request({ url: '/lims/test/method', method: 'put', data: data })
}

// 删除检验方法
export function delMethod(methodIds) {
  return request({ url: '/lims/test/method/' + methodIds, method: 'delete' })
}

// 查询质量标准列表
export function listSpecification(query) {
  return request({ url: '/lims/test/spec/list', method: 'get', params: query })
}

// 查询质量标准详情
export function getSpecification(specificationId) {
  return request({ url: '/lims/test/spec/' + specificationId, method: 'get' })
}

// 新增质量标准
export function addSpecification(data) {
  return request({ url: '/lims/test/spec', method: 'post', data: data })
}

// 修改质量标准
export function updateSpecification(data) {
  return request({ url: '/lims/test/spec', method: 'put', data: data })
}

// 删除质量标准
export function delSpecification(specificationIds) {
  return request({ url: '/lims/test/spec/' + specificationIds, method: 'delete' })
}

// 查询标准行列表
export function listSpecLines(specificationId) {
  return request({ url: '/lims/test/spec/line/list/' + specificationId, method: 'get' })
}

// 新增标准行
export function addSpecLine(data) {
  return request({ url: '/lims/test/spec/line', method: 'post', data: data })
}

// 修改标准行
export function updateSpecLine(data) {
  return request({ url: '/lims/test/spec/line', method: 'put', data: data })
}

// 删除标准行
export function delSpecLine(lineIds) {
  return request({ url: '/lims/test/spec/line/' + lineIds, method: 'delete' })
}

// 查询检验模板列表
export function listTemplate(query) {
  return request({ url: '/lims/test/template/list', method: 'get', params: query })
}

// 查询检验模板详情
export function getTemplate(templateId) {
  return request({ url: '/lims/test/template/' + templateId, method: 'get' })
}

// 新增检验模板
export function addTemplate(data) {
  return request({ url: '/lims/test/template', method: 'post', data: data })
}

// 修改检验模板
export function updateTemplate(data) {
  return request({ url: '/lims/test/template', method: 'put', data: data })
}

// 删除检验模板
export function delTemplate(templateIds) {
  return request({ url: '/lims/test/template/' + templateIds, method: 'delete' })
}

// 查询模板检测项列表
export function listTemplateItems(templateId) {
  return request({ url: '/lims/test/template/item/list/' + templateId, method: 'get' })
}

// 新增模板检测项
export function addTemplateItem(data) {
  return request({ url: '/lims/test/template/item', method: 'post', data: data })
}

// 修改模板检测项
export function updateTemplateItem(data) {
  return request({ url: '/lims/test/template/item', method: 'put', data: data })
}

// 删除模板检测项
export function delTemplateItem(ids) {
  return request({ url: '/lims/test/template/item/' + ids, method: 'delete' })
}
