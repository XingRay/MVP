package com.xingray.sample.page.main

import com.xingray.mvp.Presenter
import com.xingray.sample.lib.TaskExecutor
import java.util.*

/**
 * xxx
 *
 * @author : leixing
 * @version : 1.0.0
 * mail : leixing@baidu.com
 * @date : 2019/7/10 20:01
 */
class MainPresenter internal constructor() : Presenter<MainContract.View>(MainContract.View::class.java),
    MainContract.Presenter {

    override fun loadData() {
        view.showLoading()
        TaskExecutor.io(Runnable {
            val tests = loadTestList()
            TaskExecutor.ui(Runnable {
                view.dismissLoading()
                view.showTestList(tests)
            })
        })
    }

    private fun loadTestList(): List<Test> {
        val testList = ArrayList<Test>()

        testList.add(Test("student list test", "1"))

        return testList
    }
}
