import store from '@/store'

/**
 * 单位数据转换
 * @param {单位编码} key 
 * @returns 单位名称
 */
export function convertUnitmeasure(key) {
  let unitmeasureList = store.getters && store.getters.unitmeasureList;
  if (unitmeasureList === undefined || unitmeasureList.length === 0) {
    // 加载单位
    this.$store.dispatch('getUnitmeasureList');
  }
  if (key === undefined || key === null || key === '') {
    return '';
  }
  let unit = unitmeasureList.find(item => item.measureCode === key);
  return unit === undefined ? '' : unit.measureName;
}
