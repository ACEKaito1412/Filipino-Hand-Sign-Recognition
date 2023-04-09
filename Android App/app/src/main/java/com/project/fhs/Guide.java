package com.project.fhs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Guide extends AppCompatActivity implements View.OnClickListener {

    private static String PAGE_TITLE = "Guide";

    private ImageButton goBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

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