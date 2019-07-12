package com.xingray.sample.page.list

import android.view.View
import android.widget.TextView
import com.xingray.recycleradapter.BaseViewHolder
import com.xingray.sample.R
import com.xingray.sample.data.Student

/**
 * xxx
 *
 * @author : leixing
 * @version : 1.0.0
 * mail : leixing@baidu.com
 * @date : 2019/7/11 17:40
 */
class StudentViewHolder(itemView: View) : BaseViewHolder<Student>(itemView) {

    private val tvName: TextView = itemView.findViewById(R.id.tv_name)

    override fun bindItemView(t: Student, position: Int) {
        tvName.text = t.name
    }
}
