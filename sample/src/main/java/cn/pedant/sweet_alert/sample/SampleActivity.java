package cn.pedant.sweet_alert.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import cn.pedant.sweet_alert.Constants;
import cn.pedant.sweet_alert.SweetAlertDialog;

public class SampleActivity extends Activity implements View.OnClickListener {

    private int i = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_activity);

        int[] btnIds = {
                R.id.basic_test, R.id.under_text_test,
                R.id.error_text_test, R.id.success_text_test,
                R.id.warning_confirm_test, R.id.warning_cancel_test,
                R.id.custom_img_test, R.id.progress_dialog
        };
        for (Integer id : btnIds) {
            findViewById(id).setOnClickListener(this);
            findViewById(id).setOnTouchListener(Constants.FOCUS_TOUCH_LISTENER);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.basic_test:
                SweetAlertDialog sd = new SweetAlertDialog(this);
                sd.setCancelable(true);
                sd.setCanceledOnTouchOutside(true);
                sd.setContentText("Here's a message");
                sd.show();
                break;
            case R.id.under_text_test:
                new SweetAlertDialog(this)
                        .setTitleText("Title")
                        .setContentText("It's pretty, isn't it?")
                        .show();
                break;
            case R.id.error_text_test:
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
                break;
            case R.id.success_text_test:
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Good job!")
                        .setContentText("You clicked the button!")
                        .show();
                break;
            case R.id.warning_confirm_test:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this file!")
                        .setCancelButton("Yes, delete it!", sweetAlertDialog -> {
                            // reuse previous dialog instance
                            sweetAlertDialog.setTitleText("Deleted!")
                                    .setContentText("Your imaginary file has been deleted!")
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        })
                        .show();
                break;
            case R.id.warning_cancel_test:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this file!")
                        .setCancelText("No, cancel pls!")
                        .setConfirmText("Yes, delete it!")
                        .showCancelButton(true)
                        .setCancelClickListener(sDialog -> {
                            sDialog.setTitleText("Cancelled!")
                                    .setContentText("Your imaginary file is safe :)")
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .setCancelClickListener(null)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        })
                        .setConfirmClickListener(sDialog -> sDialog.setTitleText("Deleted!")
                                .setContentText("Your imaginary file has been deleted!")
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE))
                        .show();
                break;
            case R.id.custom_img_test:
                new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Sweet!")
                        .setContentText("Here's a custom image.")
                        .setCustomImage(R.drawable.custom_img)
                        .show();
                break;
            case R.id.progress_dialog:
                final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Loading");
                pDialog.show();
                pDialog.setCancelable(false);
                new CountDownTimer(800 * 7, 800) {
                    public void onTick(long millisUntilFinished) {
                        i++;
                        switch (i) {
                            case 0:
                                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
                                break;
                            case 1:
                                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_50));
                                break;
                            case 2:
                            case 6:
                                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                                break;
                            case 3:
                                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_20));
                                break;
                            case 4:
                                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_blue_grey_80));
                                break;
                            case 5:
                                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.warning_stroke_color));
                                break;
                        }
                    }

                    public void onFinish() {
                        i = -1;
                        pDialog.setTitleText("Success!")
                                .setConfirmText("OK")
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                }.start();
                break;
        }
    }
}