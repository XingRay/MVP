package com.xingray.mvp

/**
 * 工具类
 *
 * @author : leixing
 * @date : 2019/7/11 20:56
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
fun <T> Array<T>?.contains(element: T?): Boolean {
    if (this == null || size == 0) {
        return false
    }
    var i = 0
    val size = size
    while (i < size) {
        val t = this[i] ?: if (element == null) {
            return true
        } else {
            i++
            continue
        }

        if (t == element) {
            return true
        }
        i++
    }

    return false
}

val TAG = "sample"
