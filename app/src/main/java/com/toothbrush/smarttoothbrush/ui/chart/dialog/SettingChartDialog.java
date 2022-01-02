package com.toothbrush.smarttoothbrush.ui.chart.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.toothbrush.smarttoothbrush.databinding.DialogSettingBinding;
import com.toothbrush.smarttoothbrush.di.SoftInputUtils;

public class SettingChartDialog extends AlertDialog {


    private DialogSettingBinding binding;
    private Context context;
    private Activity activity;
    private float intervalChart;
    private OnClickSaveSettingChartListener listener;

    public SettingChartDialog(Context context, Activity activity, float intervalChart, OnClickSaveSettingChartListener listener) {
        super(context);
        this.context = context;
        this.activity = activity;

        this.intervalChart = intervalChart;
        this.listener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DialogSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();


        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 30);
            window.setBackgroundDrawable(inset);


        }


        binding.edtIntervalChart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                  SoftInputUtils.showSoftInput(activity);

                }
            }
        });


        binding.edtIntervalChart.setText(intervalChart + "");

        binding.btnCancel.setOnClickListener(view -> {
            listener.onCancel();

        });
        binding.btnSave.setOnClickListener(view -> {

            listener.onIntervalChartValue(Float.parseFloat(binding.edtIntervalChart.getText().toString()));

        });


    }


    public interface OnClickSaveSettingChartListener {
        void onIntervalChartValue(float number);

        void onCancel();
    }

}
