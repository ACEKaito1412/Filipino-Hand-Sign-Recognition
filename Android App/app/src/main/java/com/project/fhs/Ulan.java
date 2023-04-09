package com.project.fhs;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Ulan extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Ulan";

    private static String PAGE_TITLE = "Ulan Model";
    private TextView pageTitle, goBackBtn, statusTxt, responseTxt;
    private Intent intent;
    private PreviewView previewView;

    private static String prev_res = "Appending";

    private Button btnCapture;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ScheduledExecutorService scheduledExecutorService;
    private String url_link = "http://192.168.1.7:5000";

    private AtomicBoolean running = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ulan);

        pageTitle = (TextView) findViewById(R.id.PAGE_TITLE_VIEW);
        pageTitle.setText(PAGE_TITLE);

        goBackBtn = (TextView) findViewById(R.id.btnGoBack);
        goBackBtn.setOnClickListener(this);

        statusTxt = (TextView) findViewById(R.id.statusTxt);
        statusTxt.setOnClickListener(this);

        responseTxt = (TextView) findViewById(R.id.response);

        btnCapture = (Button) findViewById(R.id.capture_btn);
        btnCapture.setOnClickListener(this);

        previewView = findViewById(R.id.previewView);

        url_link = getIntent().getStringExtra("url_link");

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGoBack:
                Log.d(TAG, "ApiRequest: Clearing Request Que!!");
                frameQueue.clear();
                frameCounter = 0;
                scheduledExecutorService.shutdown();
                finish();
                break;
            case R.id.capture_btn:
                running.set(true);
                scheduledExecutorService = Executors.newScheduledThreadPool(1);
                btnCapture.setEnabled(false);
                statusTxt.setText("Status: 1000 MS");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        responseTxt.setText("");
                        statusTxt.setText("Status: START");
                        getViewBitmap();
                    }
                }, 1000);

                break;
        }

    }


    private void getViewBitmap(){
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(previewView.getBitmap() != null){
                            int newWidth = 360;
                            int newHeight = 640;
                            final Bitmap resizedImage = Bitmap.createScaledBitmap(previewView.getBitmap(), newWidth, newHeight, true);
                            ApiRequest(resizedImage);
                        }
                    }
                });
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }


    private Queue<Bitmap> frameQueue = new LinkedList<>();
    private int frameCounter = 0;

    @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(640, 360))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();


        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector,
                imageAnalysis, preview);
    }


    private void ApiRequest(Bitmap image){

        // Create a new OkHttp client
        OkHttpClient client = new OkHttpClient();

        // Create the request body
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        if (running.get()){
            // Add the frame to the queue
            frameQueue.add(image);
            frameCounter++;

            Log.d(TAG, "ApiRequest: FrameCount " + frameCounter);
            Log.d(TAG, "ApiRequest: Adding To the Que!!");

            statusTxt.setText("Status: APPENDING");

            //Check if the queue has reached 30 frames
            if (frameCounter == 15) {
                running.set(false);
                statusTxt.setText("Status: WAIT FOR RESPONSE");
                Log.d(TAG, "ApiRequest: Sending Request Que!!");
                // Add all the frames in the queue to the request
                while (!frameQueue.isEmpty()) {
                    Bitmap frame = frameQueue.poll();
                    builder.addFormDataPart("image", "image.jpg",
                            RequestBody.create(MediaType.parse("image/jpeg"), convertBitmapToByteArray(frame)));
                }

                RequestBody requestBody = builder.build();

                // Create the request
                Request request = new Request.Builder()
                        .url(url_link + "/ulan_model/")
                        .header("Content-Type", String.valueOf(requestBody.contentType()))
                        .post(requestBody)
                        .build();

                // Send the request asynchronously
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(jsonData);
                            final String result = jsonObject.getString("Result");
                            // Update the TextView on the UI thread
                            runOnUiThread(() -> {

                                statusTxt.setText("Status: RESPONSE RECEIVED");
                                btnCapture.setEnabled(true);
                                TextView textView = findViewById(R.id.response);
                                textView.setText(result);
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // Reset the counter and clear the queue
                Log.d(TAG, "ApiRequest: Clearing Request Que!!");

                scheduledExecutorService.shutdown();
                frameQueue.clear();
                frameCounter = 0;
            }

        }

    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}