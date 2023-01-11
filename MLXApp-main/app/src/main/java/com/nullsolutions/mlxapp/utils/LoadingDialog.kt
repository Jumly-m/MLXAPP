package com.nullsolutions.mlxapp.utils


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import com.nullsolutions.mlxapp.R


@SuppressLint("StaticFieldLeak")
object LoadingDialog {

    private var progressDialog: Dialog? = null
    private var activity1: Activity? = null

    fun showProgress(activity: Activity) {
        if (activity1 == null) {
            activity1 = activity
        }
        if (!activity.isFinishing) {
            if (progressDialog == null || activity !== activity1) {
                progressDialog = Dialog(activity)
                progressDialog?.setContentView(R.layout.progress_loader)
                progressDialog?.window
                    ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val dialogWindow: Window? = progressDialog!!.window
                dialogWindow?.setGravity(Gravity.CENTER)
                progressDialog!!.setCancelable(false)
                progressDialog!!.show()
            }
        }
    }

    fun dismissProgress() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
    }
}