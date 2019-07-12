package com.xingray.sample.page.main

import com.xingray.mvp.MvpPresenter
import com.xingray.mvp.MvpView

/**
 * xxx
 *
 * @author : leixing
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 * @date : 2019/7/10 20:00
 */
interface MainContract {
    interface View : MvpView<Presenter> {

        fun showLoading()

        fun dismissLoading()

        fun showTestList(tests: List<Test>)
    }

    interface Presenter : MvpPresenter<View> {

        fun loadData()
    }
}
