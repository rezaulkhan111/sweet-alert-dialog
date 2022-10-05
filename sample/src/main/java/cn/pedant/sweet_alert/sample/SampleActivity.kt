package cn.pedant.sweet_alert.sample

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import cn.pedant.SweetAlert.sample.R
import cn.pedant.sweet_alert.SweetAlertDialog
import cn.pedant.sweet_alert.SweetAlertDialog.OnSweetClickListener

class SampleActivity : Activity(), View.OnClickListener {
    private var i = -1

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_activity)
        findViewById<View>(R.id.basic_test).setOnClickListener(this)
        findViewById<View>(R.id.under_text_test).setOnClickListener(this)
        findViewById<View>(R.id.error_text_test).setOnClickListener(this)
        findViewById<View>(R.id.success_text_test).setOnClickListener(this)
        findViewById<View>(R.id.warning_confirm_test).setOnClickListener(this)
        findViewById<View>(R.id.warning_cancel_test).setOnClickListener(this)
        findViewById<View>(R.id.custom_img_test).setOnClickListener(this)
        findViewById<View>(R.id.progress_dialog).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.basic_test -> {
                // default title "Here's a message!"
                val sd = SweetAlertDialog(this)
                sd.setCancelable(true)
                sd.setCanceledOnTouchOutside(true)
                sd.show()
            }
            R.id.under_text_test -> SweetAlertDialog(this)
                .setContentText("It's pretty, isn't it?")
                .show()
            R.id.error_text_test -> SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
                .show()
            R.id.success_text_test -> SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("You clicked the button!")
                .show()
            R.id.warning_confirm_test -> SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("Yes,delete it!")
                .setConfirmClickListener(object : OnSweetClickListener {
                    override fun onClick(sDialog: SweetAlertDialog?) {
                        // reuse previous dialog instance
                        sDialog!!.setTitleText("Deleted!")
                            .setContentText("Your imaginary file has been deleted!")
                            .setConfirmText("OK")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    }
                })
                .show()
            R.id.warning_cancel_test -> SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setCancelText("No,cancel plx!")
                .setConfirmText("Yes,delete it!")
                .showCancelButton(true)
                .setCancelClickListener(object : OnSweetClickListener {
                    override fun onClick(sDialog: SweetAlertDialog?) {
                        // reuse previous dialog instance, keep widget user state, reset them if you need
                        sDialog!!.setTitleText("Cancelled!")
                            .setContentText("Your imaginary file is safe :)")
                            .setConfirmText("OK")
                            .showCancelButton(false)
                            .setCancelClickListener(null)
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE)

                        // or you can new a SweetAlertDialog to show
                        /* sDialog.dismiss();
                                new SweetAlertDialog(SampleActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Cancelled!")
                                        .setContentText("Your imaginary file is safe :)")
                                        .setConfirmText("OK")
                                        .show();*/
                    }
                })
                .setConfirmClickListener(object : OnSweetClickListener {
                    override fun onClick(sDialog: SweetAlertDialog?) {
                        sDialog!!.setTitleText("Deleted!")
                            .setContentText("Your imaginary file has been deleted!")
                            .setConfirmText("OK")
                            .showCancelButton(false)
                            .setCancelClickListener(null)
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    }
                })
                .show()
            R.id.custom_img_test -> SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Sweet!")
                .setContentText("Here's a custom image.")
                .setCustomImage(R.drawable.custom_img)
                .show()
            R.id.progress_dialog -> {
                val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText("Loading")
                pDialog.show()
                pDialog.setCancelable(false)
                object : CountDownTimer(800 * 7, 800) {
                    override fun onTick(millisUntilFinished: Long) {
                        // you can change the progress bar color by ProgressHelper every 800 millis
                        i++
                        when (i) {
                            0 -> pDialog.progressHelper.barColor =
                                resources.getColor(R.color.blue_btn_bg_color)
                            1 -> pDialog.progressHelper.barColor =
                                resources.getColor(R.color.material_deep_teal_50)
                            2, 6 -> pDialog.progressHelper.barColor =
                                resources.getColor(R.color.success_stroke_color)
                            3 -> pDialog.progressHelper.barColor =
                                resources.getColor(R.color.material_deep_teal_20)
                            4 -> pDialog.progressHelper.barColor =
                                resources.getColor(R.color.material_blue_grey_80)
                            5 -> pDialog.progressHelper.barColor =
                                resources.getColor(R.color.warning_stroke_color)
                        }
                    }

                    override fun onFinish() {
                        i = -1
                        pDialog.setTitleText("Success!")
                            .setConfirmText("OK")
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    }
                }.start()
            }
        }
    }
}