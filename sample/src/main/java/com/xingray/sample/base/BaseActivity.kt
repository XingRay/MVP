package com.xingray.sample.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * xxx
 *
 * @author : leixing
 * @date : 2019/7/19 10:11
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
abstract class BaseActivity : FragmentActivity() {

    protected lateinit var activity: Activity

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
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

    }

    /**
     * 根据调用activity的intent所携带的参数，判断activity是否可以显示
     *
     * @param intent 启动activity的参数
     * @return activity是否可以显示
     */
    protected fun isParamsValid(intent: Intent): Boolean {
        return true
    }

    /**
     * 恢复保存的状态
     *
     * @param state 保存的状态
     */
    protected fun restoreState(state: Bundle) {}

    /**
     * 初始化变量， 如presenter，adapter，数据列表等
     */
    protected abstract fun initVariables()

    /**
     * 初始化控件，在这个方法中调用[android.app.Activity.setContentView]设置布局， 绑定布局(通过
     * [android.app.Activity.findViewById]或者ButterKnife[][<a href=]//github.com/JakeWharton/butterknife"/>">&lt;a href=&quot;https://github.com/JakeWharton/butterknife&quot;/&gt;)。
     * 及设置监听器。
     */
    protected abstract fun initView()

    /**
     * 载入数据，从服务器或者本地获取数据，然后展示在页面中。
     */
    protected abstract fun loadData()
}