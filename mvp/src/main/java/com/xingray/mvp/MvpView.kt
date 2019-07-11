package com.xingray.mvp

/**
 * xxx
 *
 * @author : leixing
 * @date : 2019/7/11 20:53
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
interface MvpView<P> : LifeCycleProvider {

    val presenter: P

    fun setPresenterInterface(cls: Class<P>)

    fun hasPresenter(): Boolean

    fun bindPresenter(p: P)
}
