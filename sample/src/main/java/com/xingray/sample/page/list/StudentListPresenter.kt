package com.xingray.sample.page.list

import com.xingray.mvp.AddStrategy
import com.xingray.mvp.LifeCycle
import com.xingray.mvp.Presenter
import com.xingray.sample.data.StudentMockDataSource
import com.xingray.sample.lib.TaskExecutor

/**
 * xxx
 *
 * @author : leixing
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 * @date : 2019/7/11 11:51
 */
class StudentListPresenter internal constructor() :
    Presenter<StudentListContract.View>(StudentListContract.View::class.java), StudentListContract.Presenter {

    private val mDataSource: StudentMockDataSource = StudentMockDataSource()

    override fun loadData() {
        view.showLoading()

        TaskExecutor.io(Runnable {
            val list = mDataSource.loadStudents()
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            TaskExecutor.ui(Runnable {
                view.dismissLoading()
                view.showStudents(list)
            })
        })
    }

    override fun onStop() {
        getLifeCyclesView(AddStrategy.INSERT_TAIL, LifeCycle.STOP).scrollTo(0)
    }
}
