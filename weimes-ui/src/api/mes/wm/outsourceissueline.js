import request from '@/utils/request'

// 查询外协领料单行列表
export function listOutsourceissueline(query) {
  return request({
    url: '/mes/wm/outsourceissueline/list',
    method: 'get',
    params: query
  })
}

// 查询外协领料单行详细
export function getOutsourceissueline(lineId) {
  return request({
    url: '/mes/wm/outsourceissueline/' + lineId,
    method: 'get'
  })
}

// 新增外协领料单行
export function addOutsourceissueline(data) {
  return request({
    url: '/mes/wm/outsourceissueline',
    method: 'post',
    data: data
  })
}

// 修改外协领料单行
export function updateOutsourceissueline(data) {
  return request({
    url: '/mes/wm/outsourceissueline',
    method: 'put',
    data: data
  })
}

// 删除外协领料单行
export function delOutsourceissueline(lineId) {
  return request({
    url: '/mes/wm/outsourceissueline/' + lineId,
    method: 'delete'
  })
}
