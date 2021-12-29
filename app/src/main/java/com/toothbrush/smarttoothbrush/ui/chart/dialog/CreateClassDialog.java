package com.toothbrush.smarttoothbrush.ui.chart.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.toothbrush.smarttoothbrush.databinding.DialogClassNameBinding;

public class CreateClassDialog extends AlertDialog {

    private DialogClassNameBinding binding;
    private String userName;
    private int numberNameClass;
    private OnClickCreateClassListener listener;

    public CreateClassDialog(Context context ,String userName,int numberNameClass, OnClickCreateClassListener listener) {
        super(context);
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

        binding.edtName.setText(userName+"_class_"+numberNameClass);

        binding.btnStart.setOnClickListener(view -> {
            listener.onStart(binding.edtName.getText().toString());
        });

        binding.btnCancel.setOnClickListener(view -> listener.onCancel());


    }

    public interface OnClickCreateClassListener{
        void onStart(String address);
        void onCancel();
    }
}
