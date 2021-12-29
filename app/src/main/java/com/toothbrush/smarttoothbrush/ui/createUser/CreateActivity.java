package com.toothbrush.smarttoothbrush.ui.createUser;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.toothbrush.smarttoothbrush.R;
import com.toothbrush.smarttoothbrush.databinding.ActivityChartBinding;
import com.toothbrush.smarttoothbrush.databinding.ActivityCreateBinding;
import com.toothbrush.smarttoothbrush.ui.chart.CommunicateActivity;

import java.io.File;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.btnContinue.setOnClickListener(view -> {
            if (binding.edtName.getText().toString().equals("")) {
                Toast.makeText(this, "Name can't empty!", Toast.LENGTH_SHORT).show();
            } else if (getPermissions()) {
                openCommunicationsActivity(getIntent().getStringExtra("device_name"), getIntent().getStringExtra("device_mac"), binding.edtName.getText().toString());
            }

        });


    }

    // Called when clicking on a device entry to start the CommunicateActivity
    public void openCommunicationsActivity(String deviceName, String macAddress, String userName) {


        File folderUsername = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+getString(R.string.app_name)+"/",
                userName);
        if (!folderUsername.exists()) {
            folderUsername.mkdirs();
        }

        Intent intent = new Intent(this, CommunicateActivity.class);
        intent.putExtra("device_name", deviceName);
        intent.putExtra("device_mac", macAddress);
        intent.putExtra("user_name", userName);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean getPermissions() {

        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1001);
        }


        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Log.i("biaDige" , "omad onrequestPermission456");

        //Log.i("biaDige", "omad onrequestPermission" +requestCode+";l;");

        if (requestCode > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Log.i("biaDige", "دسترسی ثبت شد");

                File root = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
                if (!root.exists()) {
                    root.mkdirs();
                }

                openCommunicationsActivity(getIntent().getStringExtra("device_name"), getIntent().getStringExtra("device_mac"), binding.edtName.getText().toString());

            } else {

            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //Log.i("biaDige", "دسترسی ثبت نشد");
            } else {

            }


        }

    }


}