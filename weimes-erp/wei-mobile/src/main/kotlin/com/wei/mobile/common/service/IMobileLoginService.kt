package com.wei.mobile.common.service

import com.wei.mobile.common.vo.DictVo

interface IMobileLoginService {
    /**
     * 获取移动端需要的字典集合
     */
    fun getDictList(): MutableList<DictVo>
}
