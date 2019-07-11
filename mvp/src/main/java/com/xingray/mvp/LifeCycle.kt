package com.xingray.mvp

/**
 * xxx
 *
 * @author : leixing
 * @date : 2019/7/11 20:52
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
enum class LifeCycle : Comparable<LifeCycle> {
    /**
     * 初始状态
     */
    INIT,

    /**
     * 已创建
     */
    CREATE,

    /**
     * 已开始
     */
    START,

    /**
     * 开始交互
     */
    RESUME,

    /**
     * 暂停
     */
    PAUSE,

    /**
     * 停止
     */
    STOP,

    /**
     * 销毁
     */
    DESTROY
}