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

import com.toothbrush.smarttoothbrush.databinding.DialogAskExitBinding;
import com.toothbrush.smarttoothbrush.databinding.DialogClassNameBinding;
import com.toothbrush.smarttoothbrush.ui.chart.CommunicateActivity;

public class AskExitDialog extends AlertDialog {

    private DialogAskExitBinding binding;
    private OnClickAskExit listener;

    public AskExitDialog(Context context , OnClickAskExit listener) {
        super(context);
        this.listener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DialogAskExitBinding.inflate(getLayoutInflater());
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


        binding.btnStart.setOnClickListener(view -> {
            listener.onOk();
        });

        binding.btnCancel.setOnClickListener(view -> listener.onCancel());


    }

    public interface OnClickAskExit{
        void onOk();
        void onCancel();
    }
}
