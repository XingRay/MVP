package com.xingray.sample.page.list

import com.xingray.mvp.MvpPresenter
import com.xingray.mvp.MvpView
import com.xingray.sample.data.Student

/**
 * xxx
 *
 * @author : leixing
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 * @date : 2019/7/11 11:48
 */
interface StudentListContract {
    interface View : MvpView<Presenter> {

        fun showLoading()

        fun dismissLoading()

        fun showStudents(list: List<Student>)

        fun scrollTo(position: Int)
    }

    interface Presenter : MvpPresenter<View> {

        fun loadData()

        fun onStop()
    }
}
