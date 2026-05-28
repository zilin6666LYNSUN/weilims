import request from '@/utils/request'

// 查询颜色列表
export function listColor(query) {
  return request({
    url: '/md_color/color/list',
    method: 'get',
    params: query
  })
}

// 查询所有颜色列表
export function totalListColor() {
  return request({
    url: '/md_color/color/totalList',
    method: 'get',
  })
}

// 查询颜色详细
export function getColor(id) {
  return request({
    url: '/md_color/color/' + id,
    method: 'get'
  })
}

// 新增颜色
export function addColor(data) {
  return request({
    url: '/md_color/color',
    method: 'post',
    data: data
  })
}

// 修改颜色
export function updateColor(data) {
  return request({
    url: '/md_color/color',
    method: 'put',
    data: data
  })
}

// 删除颜色
export function delColor(id) {
  return request({
    url: '/md_color/color/' + id,
    method: 'delete'
  })
}
