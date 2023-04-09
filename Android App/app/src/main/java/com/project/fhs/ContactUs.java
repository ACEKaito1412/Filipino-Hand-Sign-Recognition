package com.project.fhs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ContactUs extends AppCompatActivity implements View.OnClickListener {
    private static String PAGE_TITLE = "Contact Us";

    private ImageButton goBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);


        goBackBtn = (ImageButton) findViewById(R.id.btnGoBack);
        goBackBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGoBack:
                finish();
                break;
        }
    }
}