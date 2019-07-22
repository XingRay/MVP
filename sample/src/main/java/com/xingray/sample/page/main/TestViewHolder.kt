package com.xingray.sample.page.main

import android.view.View
import android.widget.TextView
import com.xingray.recycleradapter.LayoutId
import com.xingray.recycleradapter.ViewHolder
import com.xingray.sample.R

/**
 * xxx
 *
 * @author : leixing
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 * @date : 2019/7/11 16:39
 */
@LayoutId(R.layout.item_test_list)
class TestViewHolder(itemView: View) : ViewHolder<Test>(itemView) {

    private var tvName: TextView = itemView.findViewById(R.id.tv_name)

    override fun bindItemView(t: Test, position: Int) {
        tvName.text = t.name
    }
}
