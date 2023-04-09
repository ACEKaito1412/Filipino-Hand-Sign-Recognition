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
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class aUpdate extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "aUpdate";

    private static String PAGE_TITLE = "AUpdate";
    private TextView pageTitle, goBackBtn, textView;
    private Intent intent;
    private PreviewView previewView;
    private LinearLayout updateLayout;
    private Button btnUpdate;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ScheduledExecutorService scheduledExecutorService;
    private String url_link = "http://192.168.1.7:5000/";

    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aupdate);

        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getAssets().open("tagalog2.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = stringBuilder.toString();
        list = Arrays.asList(content.split("\n"));


        pageTitle = (TextView) findViewById(R.id.PAGE_TITLE_VIEW);
        pageTitle.setText(PAGE_TITLE);

        goBackBtn = (TextView) findViewById(R.id.btnGoBack);
        goBackBtn.setOnClickListener(this);

        scheduledExecutorService = Executors.newScheduledThreadPool(1);
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

        updateLayout = (LinearLayout) findViewById(R.id.UpdateLayout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGoBack:
                finish();
                break;
        }

    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        int newWidth = 360;
        int newHeight = 640;
        Bitmap image = bitmap;
        Bitmap resizedImage = Bitmap.createScaledBitmap(image, newWidth, newHeight, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(aUpdate.this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                image.close();
            }
        });

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = previewView.getBitmap();
                        if (bitmap != null){
                            Log.d(TAG, "run: Height: " + bitmap.getHeight() + " Width" + bitmap.getWidth());
                            new ApiRequestAsyncTask().execute(bitmap);
                        }
                    }
                });
            }
        }, 0, 1600, TimeUnit.MILLISECONDS);


        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector,
                imageAnalysis, preview);
    }
    static String concatenatedString = "";
    int count2 = 0;

    private class ApiRequestAsyncTask extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap image = bitmaps[0];
            // Create a new OkHttp client
            OkHttpClient client = new OkHttpClient();

            // Create the request body
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            builder.addFormDataPart("image", "image.jpg",
                    RequestBody.create(MediaType.parse("image/jpeg"), convertBitmapToByteArray(image)));
            RequestBody requestBody = builder.build();

            // Create the request
            Request request = new Request.Builder()
                    .url(url_link + "/load_img/")
                    .header("Content-Type", String.valueOf(requestBody.contentType()))
                    .post(requestBody)
                    .build();

            // Send the request asynchronously
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);
                return jsonObject.getString("Result_2");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            TextView textView = findViewById(R.id.response);
            TextView concateView = findViewById(R.id.concateString);
            TextView sug1 = findViewById(R.id.suggest1);

            textView.setText(result);
            concatenatedString = concatenatedString + result.toString();

            List<String> resWord = new ArrayList<>();
            for (String word : list) {
                if (word.startsWith(concatenatedString)) {
                    resWord.add(word);
                }
            }

            if (count2 == 2){
                concatenatedString = "";
                concateView.setText("");
                count2 = 0;
            } else if (result.isEmpty()){
                count2 ++;
            }else{
                count2 = 0;
            }

            List<String> res = resWord.subList(0, Math.min(5, resWord.size()));

            if (res.size() > 0 && !result.isEmpty()) {
                concateView.setText(concatenatedString);
                sug1.setText(res.toString());
            }else if (result.isEmpty()){
                concatenatedString = "";
                concateView.setText("");
                sug1.setText("");
            }
            concateView.setText(concatenatedString);
        }
    }

}