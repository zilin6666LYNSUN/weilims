import request from '@/utils/request'

// 查询外协入库单行列表
export function listOursourcerecptline(query) {
  return request({
    url: '/mes/wm/oursourcerecptline/list',
    method: 'get',
    params: query
  })
}

// 查询外协入库单行详细
export function getOursourcerecptline(lineId) {
  return request({
    url: '/mes/wm/oursourcerecptline/' + lineId,
    method: 'get'
  })
}

// 新增外协入库单行
export function addOursourcerecptline(data) {
  return request({
    url: '/mes/wm/oursourcerecptline',
    method: 'post',
    data: data
  })
}

// 修改外协入库单行
export function updateOursourcerecptline(data) {
  return request({
    url: '/mes/wm/oursourcerecptline',
    method: 'put',
    data: data
  })
}

// 删除外协入库单行
export function delOursourcerecptline(lineId) {
  return request({
    url: '/mes/wm/oursourcerecptline/' + lineId,
    method: 'delete'
  })
}
