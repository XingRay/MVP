package com.xingray.sample.page.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xingray.recycleradapter.RecyclerAdapter
import com.xingray.sample.R
import com.xingray.sample.base.BaseMvpActivity
import com.xingray.sample.data.Student
import com.xingray.sample.ui.ProgressDialog
import com.xingray.sample.ui.ViewUtil

/**
 * xxx
 *
 * @author : leixing
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 * @date : 2019/7/11 11:38
 */
class StudentListActivity : BaseMvpActivity<StudentListContract.Presenter>(), StudentListContract.View {

    private var rvList: RecyclerView? = null
    private var mPresenter: StudentListPresenter? = null
    private val mProgressDialog: ProgressDialog by lazy { ProgressDialog(this) }
    private var mAdapter: RecyclerAdapter? = null

    override fun initVariables() {
        setPresenterInterface(StudentListContract.Presenter::class.java)
    }

    override fun initView() {
        setContentView(R.layout.activity_student_list)
        rvList = findViewById(R.id.rv_list)

        findViewById<View>(R.id.bt_presenter).setOnClickListener {
            val p = StudentListPresenter()
            p.bindView(this@StudentListActivity)
            bindPresenter(p)
            mPresenter = p
        }

        findViewById<View>(R.id.bt_stop).setOnClickListener { presenter.onStop() }

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

    override fun showStudents(list: List<Student>) {
        mAdapter?.update(list)
    }

    private fun initList() {
        val list = rvList ?: return
        list.layoutManager = LinearLayoutManager(mContext)

        mAdapter = RecyclerAdapter(applicationContext)
            .typeSupport(Student::class.java)
            .layoutViewSupport(R.layout.item_student_list)
            .viewHolder(StudentViewHolder::class.java)
            .itemClickListener { _, _, student -> ViewUtil.showToast(mContext, student.name) }
            .registerView().registerType()

        list.adapter = mAdapter
    }

    override fun scrollTo(position: Int) {
        rvList?.scrollToPosition(position)
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, StudentListActivity::class.java)
            if (context !is Activity) {
                starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(starter)
        }
    }
}
