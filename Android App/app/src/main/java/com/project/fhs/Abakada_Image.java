package com.project.fhs;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Abakada_Image extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Abakada_Image";

    private static String PAGE_TITLE = "Abakada Image";
    private TextView pageTitle, goBackBtn, btn_image;
    private Intent intent;
    private LinearLayout updateLayout;
    private Button btnUpdate;

    private ImageView imageView;
    private TextView txt_response;

    public static final int SELECT_IMAGE_REQUEST_CODE = 1;

    private String url_link = "http://192.168.1.7:5000";

    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abakada_image);

        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getAssets().open("tagalog.txt");
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

        url_link = getIntent().getStringExtra("url_link");

        btn_image   = (TextView) findViewById(R.id.image_btn);
        btn_image.setOnClickListener(this);

        imageView   = (ImageView) findViewById(R.id.image_view);
        txt_response= (TextView) findViewById(R.id.response);

        updateLayout = (LinearLayout) findViewById(R.id.UpdateLayout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGoBack:
                finish();
                break;
            case R.id.image_btn:
                selectImage();
                break;
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageView.setImageBitmap(bitmap);
                        new ApiRequestAsyncTask().execute(bitmap);
                    }catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
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
                return jsonObject.getString("Result");
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
            concatenatedString = concatenatedString + result;

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
            }else if (result.isEmpty()){
                concatenatedString = "";
                concateView.setText("");
            }
            sug1.setText(res.toString());
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
}