package com.xingray.mvp

/**
 * xxx
 *
 * @author : leixing
 * @date : 2019/7/11 20:56
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
class Util private constructor() {
    init {
        throw UnsupportedOperationException()
    }

    companion object {

        fun <T> contains(container: Array<T>?, element: T?): Boolean {
            if (container == null || container.size == 0) {
                return false
            }
            var i = 0
            val size = container.size
            while (i < size) {
                val t = container[i] ?: if (element == null) {
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
    }
}
