package com.xingray.sample.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.xingray.mvp.LifeCycle
import com.xingray.mvp.LifeCycleObserver
import com.xingray.mvp.MvpView
import com.xingray.mvp.PresenterHolder

/**
 * Description : activity的基类.
 *
 * @author : leixing
 * @date : 2017-04-14
 * Email       : leixing1012@qq.com
 * Version     : 0.0.1
 *
 *
 */

@Suppress("UNUSED_PARAMETER")
abstract class BaseMvpActivity<P> : FragmentActivity(), MvpView<P> {

    @Suppress("MemberVisibilityCanBePrivate")
    protected lateinit var mActivity: Activity
    protected lateinit var mContext: Context

    private val mPresenterHolder = PresenterHolder<P>()

    override val presenter: P
        get() = mPresenterHolder.presenter

    override val lifeCycle: LifeCycle
        get() = mPresenterHolder.lifeCycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
        mContext = applicationContext

        if (!isParamsValid(intent)) {
            finish()
            return
        }
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }

        initVariables()
        initView()
        loadData()

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
     * 根据调用activity的intent所携带的参数，判断activity是否可以显示
     *
     * @param intent 启动activity的参数
     * @return activity是否可以显示
     */
    protected open fun isParamsValid(intent: Intent): Boolean {
        return true
    }

    /**
     * 恢复保存的状态
     *
     * @param state 保存的状态
     */
    protected open fun restoreState(state: Bundle) {}

    /**
     * 初始化变量， 如presenter，adapter，数据列表等
     */
    protected abstract fun initVariables()

    /**
     * 初始化控件，在这个方法中调用[Activity.setContentView]设置布局， 绑定布局(通过
     * [Activity.findViewById]或者ButterKnife[][<a href=]//github.com/JakeWharton/butterknife"/>">&lt;a href=&quot;https://github.com/JakeWharton/butterknife&quot;/&gt;)。
     * 及设置监听器。
     */
    protected abstract fun initView()

    /**
     * 载入数据，从服务器或者本地获取数据，然后展示在页面中。
     */
    protected abstract fun loadData()
}
