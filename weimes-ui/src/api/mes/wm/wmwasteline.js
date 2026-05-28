import request from '@/utils/request'

// 查询生产废料单行列表
export function listWmWasteLine(query) {
  return request({
    url: '/mes/wm-waste-line/list',
    method: 'get',
    params: query
  })
}

// 查询生产废料单行详细
export function getWmWasteLine(lineId) {
  return request({
    url: '/mes/wm-waste-line/' + lineId,
    method: 'get'
  })
}

// 新增生产废料单行
export function addWmWasteLine(data) {
  return request({
    url: '/mes/wm-waste-line',
    method: 'post',
    data: data
  })
}

// 修改生产废料单行
export function updateWmWasteLine(data) {
  return request({
    url: '/mes/wm-waste-line',
    method: 'put',
    data: data
  })
}

// 删除生产废料单行
export function delWmWasteLine(lineId) {
  return request({
    url: '/mes/wm-waste-line/' + lineId,
    method: 'delete'
  })
}
