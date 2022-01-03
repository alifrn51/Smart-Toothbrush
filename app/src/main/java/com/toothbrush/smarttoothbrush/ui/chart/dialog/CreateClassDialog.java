package com.toothbrush.smarttoothbrush.ui.chart.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.toothbrush.smarttoothbrush.databinding.DialogClassNameBinding;
import com.toothbrush.smarttoothbrush.di.SoftInputUtils;

public class CreateClassDialog extends Dialog {

    private DialogClassNameBinding binding;
    private Activity activity;
    private String userName;
    private int numberNameClass;
    private OnClickCreateClassListener listener;

    public CreateClassDialog(Context context, Activity activity, String userName, int numberNameClass, OnClickCreateClassListener listener) {
        super(context);
        this.activity = activity;
        this.userName = userName;
        this.numberNameClass = numberNameClass;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DialogClassNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();


        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 30);
            window.setBackgroundDrawable(inset);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        }

        binding.edtName.setText(userName + "_class_" + numberNameClass);
        binding.edtName.requestFocus();
        binding.edtName.setSelection(binding.edtName.getText().length());
        SoftInputUtils.showSoftInput(activity);

        binding.btnStart.setOnClickListener(view -> {
            listener.onStart(binding.edtName.getText().toString());
            SoftInputUtils.showSoftInput(activity);
        });

        binding.btnCancel.setOnClickListener(view -> {
            listener.onCancel();
            SoftInputUtils.showSoftInput(activity);
        });


    }

    public interface OnClickCreateClassListener {
        void onStart(String address);

        void onCancel();
    }
}
