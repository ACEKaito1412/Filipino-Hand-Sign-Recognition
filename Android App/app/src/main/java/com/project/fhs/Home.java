package com.project.fhs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private AlertDialog.Builder dialogBuyilder;
    private AlertDialog dialog;

    private EditText apiLink;
    private Button checkBtn, closeBtn;

    private Button abakadaBtn, ulanBtn, guideBtn, tToGestureBtn, joinUsBtn, abakadaImageBtn, aUpdateBtn;
    private TextView checkApiBtn;
    private Intent intent;

    private Boolean apiStatus = false;
    private String url_link = "http://192.168.1.115:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        abakadaBtn = (Button) findViewById(R.id.btnAbakada);
        abakadaBtn.setOnClickListener(this);
        abakadaBtn.setEnabled(false);

        ulanBtn = (Button) findViewById(R.id.btnUlan);
        ulanBtn.setOnClickListener(this);
        ulanBtn.setEnabled(false);

        guideBtn = (Button) findViewById(R.id.btnGuide);
        guideBtn.setOnClickListener(this);

        tToGestureBtn = (Button) findViewById(R.id.btnTtoGesture);
        tToGestureBtn.setOnClickListener(this);
        tToGestureBtn.setEnabled(true);

        joinUsBtn = (Button) findViewById(R.id.btnJoinUs);
        joinUsBtn.setOnClickListener(this);

        abakadaImageBtn = (Button) findViewById(R.id.btnAbakadaImage);
        abakadaImageBtn.setOnClickListener(this);
        abakadaImageBtn.setEnabled(false);

        checkApiBtn = (TextView) findViewById(R.id.btnStatus);
        checkApiBtn.setOnClickListener(this);

        aUpdateBtn = (Button) findViewById(R.id.btnAUpdate);
        aUpdateBtn.setOnClickListener(this);
        aUpdateBtn.setEnabled(false);
        RequesApi();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAbakada:
                RequesApi();
                if (!apiStatus)
                {
                    intent = new Intent(this, Abakada.class);
                    intent.putExtra("url_link", url_link);
                    startActivity(intent);
                }
                break;
            case R.id.btnUlan:
                RequesApi();
                if (!apiStatus)
                {
                    intent = new Intent(this, Ulan.class);
                    intent.putExtra("url_link", url_link);
                    startActivity(intent);
                }
                break;
            case R.id.btnGuide:
                RequesApi();
                if (!apiStatus)
                {
                    intent = new Intent(this, Guide.class);
                    startActivity(intent);
                }
                break;
            case R.id.btnAUpdate:
                RequesApi();
                if (!apiStatus)
                {
                   intent = new Intent(this, aUpdate.class);
                   intent.putExtra("url_link", url_link);
                   startActivity(intent);
                }
                break;

            case R.id.btnTtoGesture:
                RequesApi();
                if (!apiStatus)
                {
                    intent = new Intent(this, TtoGesture.class);
                    startActivity(intent);
                }
                break;
            case R.id.btnJoinUs:
                intent = new Intent(this, ContactUs.class);
                startActivity(intent);
                break;
            case R.id.btnAbakadaImage:
                RequesApi();
                if (!apiStatus)
                {
                    intent = new Intent(this, Abakada_Image.class);
                    intent.putExtra("url_link", url_link);
                    startActivity(intent);
                }
                break;
            case R.id.btnStatus:
                createApiModal();
                break;
        }
    }

    public void createApiModal(){
        dialogBuyilder = new AlertDialog.Builder(this);
        final View ApiCheckModal = getLayoutInflater().inflate(R.layout.set_api, null);

        apiLink = (EditText) ApiCheckModal.findViewById(R.id.edt_api);
        checkBtn = (Button) ApiCheckModal.findViewById(R.id.btn_check);
        closeBtn = (Button) ApiCheckModal.findViewById(R.id.btn_close);

        apiLink.setText(url_link);

        dialogBuyilder.setView(ApiCheckModal);
        dialog = dialogBuyilder.show();

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (apiLink.getText().toString().isEmpty()){
                    Toast.makeText(Home.this, "Please add a link!!", Toast.LENGTH_SHORT).show();
                }else{
                    url_link = apiLink.getText().toString();
                    RequesApi();
                    dialog.dismiss();
                }
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void RequesApi(){
        // Create a new OkHttp client
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url_link)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    createApiModal();
                    Drawable drawable = getResources().getDrawable(R.drawable.offline);
                    checkApiBtn.setBackground(drawable);
                    abakadaBtn.setEnabled(false);
                    aUpdateBtn.setEnabled(false);
                    ulanBtn.setEnabled(false);
                    abakadaImageBtn.setEnabled(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() == 200){
                    runOnUiThread(() -> {
                        Drawable drawable = getResources().getDrawable(R.drawable.greenstyle);
                        checkApiBtn.setBackground(drawable);
                        abakadaBtn.setEnabled(true);
                        aUpdateBtn.setEnabled(true);
                        ulanBtn.setEnabled(true);
                        abakadaImageBtn.setEnabled(true);
                    });
                }else{
                    runOnUiThread(() -> {
                        Drawable drawable = getResources().getDrawable(R.drawable.bgstyle);
                        checkApiBtn.setBackground(drawable);
                        abakadaBtn.setEnabled(false);
                        ulanBtn.setEnabled(false);
                        abakadaImageBtn.setEnabled(false);
                        aUpdateBtn.setEnabled(false);
                    });
                }
            }
        });
    }
}