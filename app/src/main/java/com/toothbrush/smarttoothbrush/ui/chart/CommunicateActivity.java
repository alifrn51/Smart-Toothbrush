package com.toothbrush.smarttoothbrush.ui.chart;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.toothbrush.smarttoothbrush.R;
import com.toothbrush.smarttoothbrush.databinding.ActivityCommunicateBinding;
import com.toothbrush.smarttoothbrush.ui.chart.dialog.CreateClassDialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class CommunicateActivity extends AppCompatActivity {

    private EditText messageBox;
    private Button sendButton;



    private ActivityCommunicateBinding binding;

    //chart
    private LineChart chart;
    private ArrayList<Entry> values1,values2,values3;
    //chart

    private CommunicateViewModel viewModel;
    private String username;
    private CreateClassDialog dialog;
    private File gpxfile;

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

        binding.btnStart.setOnClickListener(view -> {

            dialog = new CreateClassDialog(this, username , viewModel.getNumberClassName(), new CreateClassDialog.OnClickCreateClassListener() {
                @Override
                public void onStart(String address) {

                    viewModel.setStart(true);
                    viewModel.startTimer();
                    binding.txtAddressClassName.setVisibility(View.VISIBLE);
                    binding.txtAddressClassName.setText(address);

                    binding.btnStop.setVisibility(View.VISIBLE);
                    binding.btnStart.setVisibility(View.GONE);

                    generateNoteOnSD(address, "in test azmayeshi ast");

                    dialog.dismiss();

                }

                @Override
                public void onCancel() {

                    dialog.dismiss();
                }
            });

            dialog.show();

        });
        binding.btnStop.setOnClickListener(view -> {
            viewModel.setStart(false);
            binding.btnStop.setVisibility(View.GONE);
            binding.btnStart.setVisibility(View.VISIBLE);
            binding.txtAddressClassName.setVisibility(View.GONE);
            binding.txtSec.setText(0);
            binding.txtMin.setText(0);
        });


        viewModel.getTimerLiveData().observe(this , timerApp -> {
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

    // Called when the ViewModel updates us of our connectivity status
    private void onConnectionStatus(CommunicateViewModel.ConnectionStatus connectionStatus) {
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
                break;
        }
    }

    // Called when a button in the action bar is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
        finish();
    }




    private void setData(String message) {


        LineDataSet set1, set2, set3;

        if (message.equals("No messages sent!"))
            return;

        generateNoteOnSD( "class1" , message+"\n");


        String[] parts = message.split(",");
        String part1 = parts[0]; // 004
        String part2 = parts[1];
        String part3 = parts[2];
        if (chart.getData() != null &&  chart.getData().getDataSetCount() > 0) {

            values1.add(new Entry(chart.getLineData().getEntryCount(), Float.parseFloat(part1)));
            values2.add(new Entry(chart.getLineData().getEntryCount(), Float.parseFloat(part2)));
            values3.add(new Entry(chart.getLineData().getEntryCount(), Float.parseFloat(part3) + 2.53f));

            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
            set3 = (LineDataSet) chart.getData().getDataSetByIndex(2);
            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();


            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(30);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(chart.getLineData().getEntryCount());

        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values1, "X");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setLineWidth(3f);
            set1.setColor(getResources().getColor(R.color.green));
            set1.setHighlightEnabled(false);
            set1.setDrawValues(false);
            set1.setDrawCircles(false);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);

            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            set2 = new LineDataSet(values2, "Y");
            set2.setAxisDependency(YAxis.AxisDependency.LEFT);
            set2.setLineWidth(3f);
            set2.setColor(getResources().getColor(R.color.blue));
            set2.setHighlightEnabled(false);
            set2.setDrawValues(false);
            set2.setDrawCircles(false);
            set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set2.setCubicIntensity(0.2f);
            //set2.setFillFormatter(new MyFillFormatter(900f));

            set3 = new LineDataSet(values3, "Z");
            set3.setAxisDependency(YAxis.AxisDependency.LEFT);
            set3.setLineWidth(3f);
            set3.setColor(getResources().getColor(R.color.red));
            set3.setHighlightEnabled(false);
            set3.setDrawValues(false);
            set3.setDrawCircles(false);
            set3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set3.setCubicIntensity(0.2f);

            // create a data object with the data sets
            LineData data = new LineData(set1, set2, set3);
            data.setValueTextColor(getResources().getColor(R.color.grayTxt));
            data.setValueTextSize(9f);

            // set data
            chart.setData(data);
        }
    }

    public void generateNoteOnSD(String sFileName, String sBody) {
        try {
            if (gpxfile==null)
                gpxfile = new File(
                        Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+getString(R.string.app_name)+"/"+username,
                        sFileName+".txt");
            FileWriter writer = new FileWriter(gpxfile ,true);
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
        chart.getDescription().setEnabled(true);

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
        leftAxis.setAxisMaximum(4);
        leftAxis.setAxisMinimum(-4);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setDrawBorders(false);


    }


}
