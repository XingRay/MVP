package com.xingray.sample.ui

import android.app.Dialog
import android.content.Context
import com.xingray.sample.R


/**
 * 显示加载中的dialog
 *
 * @author : leixing
 * @date : 2018/7/27 19:39
 *
 *
 * description : xxx
 */
class ProgressDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.dialog_progress)
    }
}
