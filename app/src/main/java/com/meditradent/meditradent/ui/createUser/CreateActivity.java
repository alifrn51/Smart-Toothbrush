package com.meditradent.meditradent.ui.createUser;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.meditradent.meditradent.R;
import com.meditradent.meditradent.databinding.ActivityChartBinding;
import com.meditradent.meditradent.databinding.ActivityCreateBinding;
import com.meditradent.meditradent.ui.chart.CommunicateActivity;
import com.meditradent.meditradent.ui.main.GetPermissionBluetooth;
import com.meditradent.meditradent.ui.main.MainActivity;

import java.io.File;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;

    private CreatePermissionDialog dialogPermission;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.btnContinue.setOnClickListener(view -> {
            if (binding.edtName.getText().toString().equals("")) {
                Toast.makeText(this, "Name can't empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!getPermissions())  {
                dialogPermission = new CreatePermissionDialog(this, new CreatePermissionDialog.OnClickGetPermission() {
                    @Override
                    public void onGotIt() {


                        getPermissions();
                        dialogPermission.dismiss();

                    }

                    @Override
                    public void onClose() {

                        finish();
                    }
                });
            }

        });


    }

    // Called when clicking on a device entry to start the CommunicateActivity
    public void openCommunicationsActivity(String deviceName, String macAddress, String userName) {


        File folderUsername = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + getString(R.string.app_name) + "/",
                userName);
        if (!folderUsername.exists()) {
            folderUsername.mkdirs();
        }

        binding.edtName.setText("");
        Intent intent = new Intent(this, CommunicateActivity.class);
        intent.putExtra("device_name", deviceName);
        intent.putExtra("device_mac", macAddress);
        intent.putExtra("user_name", userName);
        startActivity(intent);
    }



    private boolean getPermissions() {

        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};


        if (ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.

            openCommunicationsActivity(getIntent().getStringExtra("device_name"), getIntent().getStringExtra("device_mac"), binding.edtName.getText().toString());


            return true;

        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            activityResultLauncher.launch(permissions);

        }


        return false;
    }

    private final ActivityResultLauncher<String[]> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {

                        if (result == null) {
                            return;
                        }


                        for (Boolean isGranted : result.values()) {
                            if (isGranted) {
                                //openCommunicationsActivity(getIntent().getStringExtra("device_name"), getIntent().getStringExtra("device_mac"), binding.edtName.getText().toString());

                            } else {
                                if (dialogPermission!=null){
                                    dialogPermission.show();
                                }
                                // getPermissions();

                            }
                        }
                    });








}