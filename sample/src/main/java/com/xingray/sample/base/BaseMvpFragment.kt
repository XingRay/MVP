package com.xingray.sample.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xingray.mvp.LifeCycle
import com.xingray.mvp.LifeCycleObserver
import com.xingray.mvp.MvpView
import com.xingray.mvp.PresenterHolder

/**
 * @author : leixing
 * @date : 2017-04-20
 * Email       : leixing1012@qq.com
 * Version     : 0.0.1
 *
 *
 * Description : xxx
 */

abstract class BaseMvpFragment<P> : Fragment(), MvpView<P> {

    private var mRootView: View? = null

    @Suppress("MemberVisibilityCanBePrivate")
    protected var mActivity: Activity? = null

    @Suppress("MemberVisibilityCanBePrivate")
    protected var mContext: Context? = null

    private var mPresenterHolder = PresenterHolder<P>()

    override val presenter: P
        get() = mPresenterHolder.presenter

    override val lifeCycle: LifeCycle
        get() = mPresenterHolder.lifeCycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity
        mContext = context
        mPresenterHolder = PresenterHolder()

        initVariables(arguments)

        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }

        mPresenterHolder.notifyLifeCycleChanged(LifeCycle.CREATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = initView(inflater, container)
        return mRootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadData()
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
        super.onPause()
        mPresenterHolder.notifyLifeCycleChanged(LifeCycle.PAUSE)
    }

    override fun onStop() {
        super.onStop()
        mPresenterHolder.notifyLifeCycleChanged(LifeCycle.STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenterHolder.notifyLifeCycleChanged(LifeCycle.DESTROY)
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

    /**
     * 初始化变量
     *
     * @param arguments 外部传入的参数
     */
    protected abstract fun initVariables(arguments: Bundle?)

    /**
     * 恢复保存的状态
     *
     * @param state 状态数据
     */
    protected open fun restoreState(state: Bundle) {

    }

    /**
     * 初始化视图
     *
     * @param inflater  inflater
     * @param container container
     * @return 加载的视图
     */
    protected abstract fun initView(inflater: LayoutInflater, container: ViewGroup?): View

    /**
     * 加载数据
     */
    protected abstract fun loadData()
}
