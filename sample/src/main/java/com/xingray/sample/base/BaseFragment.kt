package com.xingray.sample.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * xxx
 *
 * @author : leixing
 * @date : 2019/7/19 10:11
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
abstract class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVariables(arguments)

        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView(inflater, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadData()
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
    protected fun restoreState(state: Bundle) {

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