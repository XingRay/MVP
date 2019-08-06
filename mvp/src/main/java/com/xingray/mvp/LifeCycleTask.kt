package com.xingray.mvp

/**
 * 页面生命周期相关任务
 *
 * @author : leixing
 * @date : 2019/8/6 12:55
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
data class LifeCycleTask(
    val lifeCycles: Array<LifeCycle>,
    val task: () -> Unit
)