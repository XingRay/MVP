package com.xingray.mvp

/**
 * View的声明周期观察者接口
 *
 * @author : leixing
 * @date : 2019/7/11 20:52
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
interface LifeCycleObserver {
    /**
     * [LifeCycle] 变化时回调
     */
    fun onLifeCycleChanged(lifeCycle: LifeCycle)
}
