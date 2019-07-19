package com.xingray.sample.page.main

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xingray.recycleradapter.RecyclerAdapter
import com.xingray.sample.R
import com.xingray.sample.base.BaseMvpActivity
import com.xingray.sample.page.list.StudentListActivity
import com.xingray.sample.ui.ProgressDialog
import java.util.ArrayList

/**
 * @author leixing
 */
class MainActivity : BaseMvpActivity<MainContract.Presenter>(), MainContract.View {

    private var rvList: RecyclerView? = null
    private var mAdapter: RecyclerAdapter? = null
    private val mProgressDialog: ProgressDialog by lazy { ProgressDialog(this) }

    override fun initVariables() {
        setPresenterInterface(MainContract.Presenter::class.java)
        val p = MainPresenter()
        bindPresenter(p)
        p.bindView(this)
    }

    override fun initView() {
        setContentView(R.layout.activity_main)

        rvList = findViewById(R.id.rv_list)
        initList()
    }

    override fun loadData() {
        presenter.loadData()
    }

    override fun showLoading() {
        mProgressDialog.show()
    }

    override fun dismissLoading() {
        mProgressDialog.dismiss()
    }

    override fun showTestList(tests: List<Test>) {
        mAdapter?.update(tests)
    }

    private fun initList() {
        val list = rvList ?: return
        list.layoutManager = LinearLayoutManager(applicationContext)

        mAdapter = RecyclerAdapter(applicationContext)
            .typeSupport(Test::class.java)
            .layoutViewSupport(R.layout.item_test_list)
            .viewHolder(TestViewHolder::class.java)
            .itemClickListener { _, _, test -> gotoTestPage(test) }
            .registerView().registerType()

        list.adapter = mAdapter
    }

    private fun loadTestList(): List<Test> {
        val testList = ArrayList<Test>()

        testList.add(Test("student list test", "1"))

        return testList
    }

    private fun gotoTestPage(test: Test) {
        when (test.id) {
            "1" -> StudentListActivity.start(this)
        }
    }


}
