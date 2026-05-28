import request from '@/utils/request'

// 查询物料列表
export function listMdItem(query) {
  return request({
    url: '/mes/md/mditem/list',
    method: 'get',
    params: query
  })
}

// 查询物料详细
export function getMdItem(itemId) {
  return request({
    url: '/mes/md/mditem/' + itemId,
    method: 'get'
  })
}

// 新增物料
export function addMdItem(data) {
  return request({
    url: '/mes/md/mditem',
    method: 'post',
    data: data
  })
}

/**
 * 新增产品或物料
 * type product:产品 item:物料
 */
export function addItemProduct(data,type) {
  return request({
    url: '/mes/md/mditem/product/' + type,
    method: 'post',
    data: data
  })
}

// 修改物料
export function updateMdItem(data) {
  return request({
    url: '/mes/md/mditem',
    method: 'put',
    data: data
  })
}

// 删除物料
export function delMdItem(itemId) {
  return request({
    url: '/mes/md/mditem/' + itemId,
    method: 'delete'
  })
}

/**
 * 查询产品/物料
 * type product:产品 item:物料
 */
export function listMdItemProduct(query,type) {
  return request({
    url: '/mes/md/mditem/list/product/' + type,
    method: 'get',
    params: query
  })
}
