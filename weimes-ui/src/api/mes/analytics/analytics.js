import request from '@/utils/request'
// 查询库存数据
export function getStock(query) {
    return request({
        url: '/analytics/getStock',
        method: 'get',
        params: query
    })
}
// 查询生产数据
export function getWorkorder(query) {
    return request({
        url: '/analytics/getWorkorder',
        method: 'get',
        params: query
    })
}
// 查询物料数据
export function getItem(query) {
    return request({
        url: '/analytics/getItem',
        method: 'get',
        params: query
    })
}
// 查询物料分类子级
export function getItemType() {
    return request({
        url: '/mes/md/itemtype/selectChildrenByAncestor',
        method: 'get'
    })
}