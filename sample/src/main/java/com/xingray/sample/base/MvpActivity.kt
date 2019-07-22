package com.xingray.sample.base

import android.os.Bundle
import com.xingray.mvp.LifeCycle
import com.xingray.mvp.LifeCycleObserver
import com.xingray.mvp.MvpView
import com.xingray.mvp.PresenterHolder

/**
 *  `MVP`模式的`Activity`基类.
 *
 * @author : leixing
 * @date : 2017-04-14
 * Email       : leixing1012@qq.com
 * Version     : 0.0.1
 *
 */

@Suppress("UNUSED_PARAMETER")
abstract class MvpActivity<P> : BaseActivity(), MvpView<P> {

    private val mPresenterHolder = PresenterHolder<P>()

    override val presenter: P
        get() = mPresenterHolder.presenter

    override val lifeCycle: LifeCycle
        get() = mPresenterHolder.lifeCycle

    override fun notifyLifeCycleChanged(lifeCycle: LifeCycle) {
        mPresenterHolder.notifyLifeCycleChanged(lifeCycle)
    }

    override fun setPresenterInterface(cls: Class<P>) {
        mPresenterHolder.setPresenterInterface(cls)
    }

    override fun hasPresenter(): Boolean {
        return mPresenterHolder.hasPresenter()
    }

    override fun bindPresenter(p: P) {
        mPresenterHolder.bindPresenter(p)
    }

    override fun addLifeCycleObserver(observer: LifeCycleObserver) {
        mPresenterHolder.addLifeCycleObserver(observer)
    }

    override fun removeLifeCycleObserver(observer: LifeCycleObserver) {
        mPresenterHolder.removeLifeCycleObserver(observer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenterHolder.notifyLifeCycleChanged(LifeCycle.CREATE)
    }

    override fun onStart() {
        super.onStart()
        mPresenterHolder.notifyLifeCycleChanged(LifeCycle.START)
    }

    override fun onResume() {
        super.onResume()
        mPresenterHolder.notifyLifeCycleChanged(LifeCycle.RESUME)
    }

    override fun onPause() {
        mPresenterHolder.notifyLifeCycleChanged(LifeCycle.PAUSE)
        super.onPause()
    }

    override fun onStop() {
        mPresenterHolder.notifyLifeCycleChanged(LifeCycle.STOP)
        super.onStop()
    }

    override fun onDestroy() {
        mPresenterHolder.notifyLifeCycleChanged(LifeCycle.DESTROY)
        super.onDestroy()
    }
}
