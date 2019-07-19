package com.xingray.mvp

/**
 * View的生命周期提供者的接口
 *
 * @author : leixing
 * @date : 2019/7/11 20:53
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
interface LifeCycleProvider {


    /**
     * View当前的生命周期状态
     *
     * @return 生命周期状态
     */
    val lifeCycle: LifeCycle

    /**
     * 通知[LifeCycle]已经变化
     */
    fun notifyLifeCycleChanged(lifeCycle: LifeCycle)

    /**
     * 添加生命周期观察者
     *
     * @param observer 生命周期观察者
     */
    fun addLifeCycleObserver(observer: LifeCycleObserver)

    /**
     * 删除生命周期观察者
     *
     * @param observer 生命周期观察者
     */
    fun removeLifeCycleObserver(observer: LifeCycleObserver)
}