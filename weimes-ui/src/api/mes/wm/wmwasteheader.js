import request from '@/utils/request'

// 查询生产废料单头列表
export function listWmWasteHeader(query) {
  return request({
    url: '/mes/wm-waste-header/list',
    method: 'get',
    params: query
  })
}

// 查询生产废料单头详细
export function getWmWasteHeader(wasteId) {
  return request({
    url: '/mes/wm-waste-header/' + wasteId,
    method: 'get'
  })
}

// 新增生产废料单头
export function addWmWasteHeader(data) {
  return request({
    url: '/mes/wm-waste-header',
    method: 'post',
    data: data
  })
}

// 修改生产废料单头
export function updateWmWasteHeader(data) {
  return request({
    url: '/mes/wm-waste-header',
    method: 'put',
    data: data
  })
}

// 删除生产废料单头
export function delWmWasteHeader(wasteId) {
  return request({
    url: '/mes/wm-waste-header/' + wasteId,
    method: 'delete'
  })
}

//执行废料
export function execute(wasteId){
  return request({
    url: '/mes/wm-waste-header/' + wasteId,
    method: 'put'
  })
}
