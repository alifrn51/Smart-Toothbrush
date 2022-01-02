package com.toothbrush.smarttoothbrush.ui.chart;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.toothbrush.smarttoothbrush.R;
import com.toothbrush.smarttoothbrush.databinding.ActivityCommunicateBinding;
import com.toothbrush.smarttoothbrush.databinding.DialogSettingBinding;
import com.toothbrush.smarttoothbrush.di.KeyS;
import com.toothbrush.smarttoothbrush.di.SoftInputUtils;
import com.toothbrush.smarttoothbrush.di.sharepreferences.MyPref;
import com.toothbrush.smarttoothbrush.ui.chart.dialog.AskExitDialog;
import com.toothbrush.smarttoothbrush.ui.chart.dialog.CreateClassDialog;
import com.toothbrush.smarttoothbrush.ui.chart.dialog.SettingChartDialog;
import com.waspar.falert.DoubleButtonListener;
import com.waspar.falert.Falert;
import com.waspar.falert.FalertButtonType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class CommunicateActivity extends AppCompatActivity {

    private EditText messageBox;
    private Button sendButton;


    private ActivityCommunicateBinding binding;

    //chart
    private LineChart chart;
    private ArrayList<Entry> values1, values2, values3;
    //chart

    private CommunicateViewModel viewModel;
    private String username;
    private CreateClassDialog dialog;
    private File gpxfile;
    private String addressClassData = "s";
    private CommunicateViewModel.ConnectionStatus connectionStatusForStart;
    private AskExitDialog dialogExit;
    private float intervalChartValue;
    private SettingChartDialog dialogSetting;
    private LineDataSet setX, setY, setZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup our activity
        super.onCreate(savedInstanceState);
        binding = ActivityCommunicateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Enable the back button in the action bar if possible
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        intervalChartValue = MyPref.read(KeyS.INTERVAL_VALUE, 9f);

        username = getIntent().getStringExtra("user_name");
        binding.txtUserName.setText(username);
        // Setup our ViewModel
        viewModel = new ViewModelProvider(this).get(CommunicateViewModel.class);


        values1 = new ArrayList<>();
        values2 = new ArrayList<>();
        values3 = new ArrayList<>();

        chart = binding.chart1;
        initChart();


        // This method return false if there is an error, so if it does, we should close.
        if (!viewModel.setupViewModel(getIntent().getStringExtra("device_name"), getIntent().getStringExtra("device_mac"))) {
            finish();
            return;
        }

        binding.txtExit.setOnClickListener(view -> {
            dialogExit = new AskExitDialog(this, new AskExitDialog.OnClickAskExit() {
                @Override
                public void onOk() {
                    finish();
                }

                @Override
                public void onCancel() {
                    dialogExit.dismiss();
                }
            });

            dialogExit.show();
        });

        binding.btnStart.setOnClickListener(view -> {
            viewModel.connect();


            onConnectionStatusForStart(connectionStatusForStart);

        });
        binding.btnStop.setOnClickListener(view -> {
            stopWritingDAta();
        });


        binding.checkX.setChecked(MyPref.read(KeyS.IS_CHECKED_X, true));
        binding.checkZ.setChecked(MyPref.read(KeyS.IS_CHECKED_Y, true));
        binding.checkY.setChecked(MyPref.read(KeyS.IS_CHECKED_Z, true));

        binding.checkX.setOnCheckedChangeListener((compoundButton, b) -> {
            if (setX != null)
                setX.setVisible(b);
            MyPref.write(KeyS.IS_CHECKED_X, b);
        });

        binding.checkY.setOnCheckedChangeListener((compoundButton, b) -> {
            if (setY != null)
                setY.setVisible(b);
            MyPref.write(KeyS.IS_CHECKED_Y, b);
        });

        binding.checkZ.setOnCheckedChangeListener((compoundButton, b) -> {
            if (setZ != null)
                setZ.setVisible(b);
            MyPref.write(KeyS.IS_CHECKED_Z, b);
        });


        binding.btnSetting.setOnClickListener(view -> {

            dialogSetting = new SettingChartDialog(this, this, intervalChartValue, new SettingChartDialog.OnClickSaveSettingChartListener() {
                @Override
                public void onIntervalChartValue(float number) {

                    MyPref.write(KeyS.INTERVAL_VALUE, number);
                    intervalChartValue = number;
                    YAxis leftAxis = chart.getAxisLeft();
                    leftAxis.setAxisMaximum(number);
                    leftAxis.setAxisMinimum(number * -1f);

                    chart.invalidate();
                    chart.notifyDataSetChanged();

                    dialogSetting.dismiss();

                }

                @Override
                public void onCancel() {
                    dialogSetting.dismiss();
                }
            });


            dialogSetting.show();


        });


        viewModel.getTimerLiveData().observe(this, timerApp -> {
            binding.txtSec.setText(timerApp.getSeconds());
            binding.txtMin.setText(timerApp.getMinutes());
        });
        // Setup our Views
        //messagesView = findViewById(R.id.communicate_messages);
        messageBox = binding.communicateMessage;
        sendButton = binding.communicateSend;

        // Start observing the data sent to us by the ViewModel
        viewModel.getConnectionStatus().observe(this, this::onConnectionStatus);
        viewModel.getDeviceName().observe(this, name -> setTitle(getString(R.string.device_name_format, name)));
        viewModel.getMessages().observe(this, message -> {
            if (TextUtils.isEmpty(message)) {
                message = getString(R.string.no_messages);
            }
            //messagesView.setText(message);
            setData(message);
        });
        viewModel.getMessage().observe(this, message -> {
            // Only update the message if the ViewModel is trying to reset it
            if (TextUtils.isEmpty(message)) {
                messageBox.setText(message);
            }
        });

        // Setup the send button click action
        sendButton.setOnClickListener(v -> viewModel.sendMessage(messageBox.getText().toString()));
    }


    private void onConnectionStatusForStart(CommunicateViewModel.ConnectionStatus connectionStatus) {

        if (connectionStatus == null) {
            Toast.makeText(this, "Please connect device!", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (connectionStatus) {
            case CONNECTED:
                showDialogAddClass();
                break;

            case CONNECTING:
                Toast.makeText(this, "Please wait!", Toast.LENGTH_SHORT).show();
                break;

            case DISCONNECTED:
                Toast.makeText(this, "Device disconnected", Toast.LENGTH_SHORT).show();
                break;
        }


    }

    // Called when the ViewModel updates us of our connectivity status
    private void onConnectionStatus(CommunicateViewModel.ConnectionStatus connectionStatus) {
        connectionStatusForStart = connectionStatus;
        switch (connectionStatus) {
            case CONNECTED:

                messageBox.setEnabled(true);
                sendButton.setEnabled(true);
                binding.communicateConnect.setEnabled(true);

                binding.communicateConnect.setAlpha(1f);
                binding.communicateConnect.setImageResource(R.drawable.ic_connect);
                binding.communicateConnect.setOnClickListener(v -> viewModel.disconnect());
                break;

            case CONNECTING:

                messageBox.setEnabled(false);
                sendButton.setEnabled(false);
                binding.communicateConnect.setEnabled(false);
                binding.communicateConnect.setAlpha(0.2f);
                binding.communicateConnect.setImageResource(R.drawable.ic_connect);
                break;

            case DISCONNECTED:


                messageBox.setEnabled(false);
                sendButton.setEnabled(false);
                binding.communicateConnect.setAlpha(1f);
                binding.communicateConnect.setEnabled(true);
                binding.communicateConnect.setImageResource(R.drawable.ic_disconnect);
                binding.communicateConnect.setOnClickListener(v -> viewModel.connect());
                stopWritingDAta();


                break;
        }
    }

    private void stopWritingDAta() {


        if (viewModel.isSaveData())
            generateNoteOnSD(addressClassData, "--------------------------  " + "\n\r" +
                    "Date: " + Calendar.getInstance().getTime() + "\n\r" + "End log file");

        viewModel.setSaveData(false);
        viewModel.setStart(false);
        gpxfile = null;
        binding.btnStop.setVisibility(View.GONE);
        binding.btnStart.setVisibility(View.VISIBLE);
        binding.txtAddressClassName.setVisibility(View.GONE);
        binding.txtSec.setText("00");
        binding.txtMin.setText("00");

        binding.txtExit.setEnabled(true);
        binding.txtExit.setAlpha(1f);
    }

    private void showDialogAddClass() {


        dialog = new CreateClassDialog(this, username, viewModel.getNumberClassName(), new CreateClassDialog.OnClickCreateClassListener() {
            @Override
            public void onStart(String address) {

                viewModel.setNumberClassName();

                binding.txtExit.setEnabled(false);
                binding.txtExit.setAlpha(0.5f);

                addressClassData = address;
                viewModel.setStart(true);
                viewModel.startTimer();
                binding.txtAddressClassName.setVisibility(View.VISIBLE);
                binding.txtAddressClassName.setText(address);

                binding.btnStop.setVisibility(View.VISIBLE);
                binding.btnStart.setVisibility(View.GONE);

                generateNoteOnSD(address, "Terminal log file " + username + "\n\r" +
                        "Date: " + Calendar.getInstance().getTime() + "\n\r" + "--------------------------");

                viewModel.setSaveData(true);
                dialog.dismiss();

            }

            @Override
            public void onCancel() {
                viewModel.setSaveData(false);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // Called when a button in the action bar is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // If the back button was pressed, handle it the normal way
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Called when the user presses the back button
    @Override
    public void onBackPressed() {
        // Close the activity
        dialogExit = new AskExitDialog(this, new AskExitDialog.OnClickAskExit() {
            @Override
            public void onOk() {
                stopWritingDAta();
                finish();
            }

            @Override
            public void onCancel() {
                dialogExit.dismiss();
            }
        });
        dialogExit.show();
    }


    private void setData(String message) {


        if (message.equals("No messages sent!"))
            return;


        if (viewModel.isSaveData())
            generateNoteOnSD(addressClassData, message);


        String[] parts = message.split(",");
        String part1 = parts[0]; // 004
        String part2 = parts[1];
        String part3 = parts[2];
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {

            values1.add(new Entry(chart.getLineData().getEntryCount(), Float.parseFloat(part1)));
            values2.add(new Entry(chart.getLineData().getEntryCount(), Float.parseFloat(part2)));
            values3.add(new Entry(chart.getLineData().getEntryCount(), Float.parseFloat(part3) + 2.53f));

            setX = (LineDataSet) chart.getData().getDataSetByIndex(0);
            setY = (LineDataSet) chart.getData().getDataSetByIndex(1);
            setZ = (LineDataSet) chart.getData().getDataSetByIndex(2);
            setX.setValues(values1);
            setY.setValues(values2);
            setZ.setValues(values3);


            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();


            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(30);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(chart.getLineData().getEntryCount());

        } else {
            // create a dataset and give it a type
            setX = new LineDataSet(values1, "X");

            setX.setAxisDependency(YAxis.AxisDependency.LEFT);
            setX.setLineWidth(3f);
            setX.setColor(getResources().getColor(R.color.green));
            setX.setHighlightEnabled(false);
            setX.setDrawValues(false);
            setX.setDrawCircles(false);
            setX.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            setX.setCubicIntensity(0.2f);

            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            setY = new LineDataSet(values2, "Y");
            setY.setAxisDependency(YAxis.AxisDependency.LEFT);
            setY.setLineWidth(3f);
            setY.setColor(getResources().getColor(R.color.blue));
            setY.setHighlightEnabled(false);
            setY.setDrawValues(false);
            setY.setDrawCircles(false);
            setY.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            setY.setCubicIntensity(0.2f);
            //set2.setFillFormatter(new MyFillFormatter(900f));

            setZ = new LineDataSet(values3, "Z");
            setZ.setAxisDependency(YAxis.AxisDependency.LEFT);
            setZ.setLineWidth(3f);
            setZ.setColor(getResources().getColor(R.color.red));
            setZ.setHighlightEnabled(false);
            setZ.setDrawValues(false);
            setZ.setDrawCircles(false);
            setZ.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            setZ.setCubicIntensity(0.2f);

            // create a data object with the data sets
            LineData data = new LineData(setX, setY, setZ);
            data.setValueTextColor(getResources().getColor(R.color.grayTxt));
            data.setValueTextSize(9f);


            setX.setVisible(binding.checkX.isChecked());
            setY.setVisible(binding.checkY.isChecked());
            setZ.setVisible(binding.checkZ.isChecked());

            // set data
            chart.setData(data);
        }
    }

    public void generateNoteOnSD(String sFileName, String sBody) {
        try {
            if (gpxfile == null)
                gpxfile = new File(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name) + "/" + username,
                        sFileName + ".txt");
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(sBody).append("\n\r");
            writer.flush();
            writer.close();
            // Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initChart() {
        // enable description text
        chart.getDescription().setEnabled(false);
        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(getResources().getColor(R.color.grayTxt));

        // add empty data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(getResources().getColor(R.color.grayTxt));

        XAxis xl = chart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(R.color.grayTxt));
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(intervalChartValue);
        leftAxis.setAxisMinimum(intervalChartValue * -1f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setDrawBorders(false);


    }


}
