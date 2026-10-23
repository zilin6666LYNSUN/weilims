/**
 * 菜单标题翻译工具（方案 A 前端映射表）
 * 语言包内置 menu.* keys 映射数据库 sys_menu 中文菜单名；
 * 渲染动态菜单 / 面包屑 / TagsView 时调用，未映射的标题回退显示中文。
 */
export function translateTitle(vm, title) {
  if (!title || typeof title !== 'string') {
    return title
  }
  const key = 'menu.' + title
  if (vm && typeof vm.$te === 'function' && vm.$te(key)) {
    return vm.$t(key)
  }
  return title
}

/**
 * 字典标签翻译（方案 A 前端映射表）
 * 语言包内置 dict.* keys（如 dict.sys_normal_disable.0 = 正常），
 * 渲染字典标签（DictTag / 下拉选项）时调用，未映射的标签回退显示中文。
 */
export function translateDictLabel(vm, dictType, value) {
  if (!dictType || value === undefined || value === null || value === '') {
    return value
  }
  const key = 'dict.' + dictType + '.' + value
  if (vm && typeof vm.$te === 'function' && vm.$te(key)) {
    return vm.$t(key)
  }
  return value
}

/**
 * 为无实例上下文场景提供的字典标签翻译函数
 */
export function createDictTranslator(vm) {
  return (dictType, value) => translateDictLabel(vm, dictType, value)
}

/**
 * 为无实例上下文场景提供的翻译函数（如 functional 组件）
 */
export function createTitleTranslator(vm) {
  return title => translateTitle(vm, title)
}
