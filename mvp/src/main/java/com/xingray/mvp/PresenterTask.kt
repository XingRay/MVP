package com.xingray.mvp

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

/**
 * xxx
 *
 * @author : leixing
 * @date : 2019/7/11 20:51
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
class PresenterTask<V : LifeCycleProvider> internal constructor(
    private val presenter: Presenter<V>,
    private val method: Method,
    private val args: Array<Any>?,
    internal val lifeCycles: Array<LifeCycle>?
) : Runnable {

    override fun run() {
        try {
            method.invoke(presenter.targetView, *(args ?: arrayOfNulls(0)))
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as PresenterTask<*>
        return method == that.method && Arrays.equals(lifeCycles, that.lifeCycles)
    }

    override fun hashCode(): Int {
        var result = method.name.hashCode()
        result = 31 * result + Arrays.hashCode(lifeCycles)
        return result
    }
}
