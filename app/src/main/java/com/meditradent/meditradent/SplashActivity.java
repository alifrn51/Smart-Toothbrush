package com.meditradent.meditradent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.meditradent.meditradent.ui.main.GetPermissionBluetooth;
import com.meditradent.meditradent.ui.main.MainActivity;

@RequiresApi(api = Build.VERSION_CODES.S)
public class SplashActivity extends AppCompatActivity {


    private GetPermissionBluetooth dialogPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!getPermissions()) {
                dialogPermission = new GetPermissionBluetooth(this, new GetPermissionBluetooth.OnClickGetPermission() {
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
                dialogPermission.show();
                return;

            }
        } else {

            startActivity(new Intent(this, MainActivity.class));
            finish();

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private boolean getPermissions() {

        String[] permissions = {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};


        if (ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.

            startActivity(new Intent(this, MainActivity.class));
            finish();

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
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                if (dialogPermission!=null){
                                    dialogPermission.show();
                                }
                               // getPermissions();

                            }
                        }
                    });




}