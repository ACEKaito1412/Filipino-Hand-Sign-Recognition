package com.amore.fsltranslator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NurseryRecognition extends CameraActivity {

    CameraBridgeViewBase cameraBridgeViewBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursery_recognition);

        /*OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://192.168.100.42:5000/").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(NurseryRecognition.this, "network not found",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });*/


        if (OpenCVLoader.initDebug()) Log.d("Loaded", "success");
        else Log.d("Loaded", "error");

        //cameraPermission
        getPermission();

        cameraBridgeViewBase = findViewById(R.id.cameraView);
        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            long previousTime = System.currentTimeMillis();
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                long currentTime = System.currentTimeMillis();
                Mat frame = inputFrame.rgba();
                if (currentTime - previousTime >= 1000) {
                    previousTime = currentTime;
                    // code to send the frame

                    sendFrameToServer(frame);
                    Log.d("sendFrameToServer", "Meron ?");

                }
                return frame;
            }
            private void sendFrameToServer(Mat frame) {
                // Convert the frame to a format that can be sent over the network
                MatOfByte matOfByte = new MatOfByte();
                Imgcodecs.imencode(".jpg", frame, matOfByte);
                byte[] byteArray = matOfByte.toArray();


                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);

                // Create the OkHttp client
                OkHttpClient client = new OkHttpClient();

                // Create the request
                Request request = new Request.Builder()
                        .url("http://192.168.100.42:5000/load_img/")
                        .post(requestBody)
                        .build();

                // Send the request
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(NurseryRecognition.this, "YEEEEY!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(NurseryRecognition.this, "Ney :(", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }

                });

        if(OpenCVLoader.initDebug()){
            cameraBridgeViewBase.enableView();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        cameraBridgeViewBase.enableView();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
    }
    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }

    void getPermission(){
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
    }
    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0]!=PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }
    }
}