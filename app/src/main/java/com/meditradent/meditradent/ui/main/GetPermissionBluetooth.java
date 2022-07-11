package com.meditradent.meditradent.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.meditradent.meditradent.databinding.DialogAskExitBinding;
import com.meditradent.meditradent.databinding.DialogGetPermissionBinding;

public class GetPermissionBluetooth extends AlertDialog {

    private DialogGetPermissionBinding binding;
    private OnClickGetPermission listener;

    public GetPermissionBluetooth(Context context , OnClickGetPermission listener) {
        super(context);
        this.listener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DialogGetPermissionBinding.inflate(getLayoutInflater());
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

        setCancelable(false);

        binding.btnGotIt.setOnClickListener(view -> {
            listener.onGotIt();
        });

        binding.btnClose.setOnClickListener(view -> listener.onClose());


    }

    public interface OnClickGetPermission{
        void onGotIt();
        void onClose();
    }
}
