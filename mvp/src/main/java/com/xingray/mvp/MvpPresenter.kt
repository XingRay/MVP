package com.xingray.mvp

/**
 * {@code Presenter}的公共基础接口
 *
 * @author : leixing
 * @date : 2019/7/11 20:53
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
interface MvpPresenter<V : LifeCycleProvider> {

    fun bindView(view: V)
}
