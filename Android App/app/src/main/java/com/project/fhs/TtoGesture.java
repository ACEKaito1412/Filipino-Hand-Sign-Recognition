package com.project.fhs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TtoGesture extends AppCompatActivity implements View.OnClickListener {

    private static String PAGE_TITLE = "Text To Gesture";

    private TextView pageTitle, goBackBtn;

    private Intent intent;

    private AutoCompleteTextView autoComplete;

    private Button btn_check;

    List<String> list;

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<Integer> mImageId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tto_gesture);

        pageTitle = (TextView) findViewById(R.id.PAGE_TITLE_VIEW);
        pageTitle.setText(PAGE_TITLE);

        goBackBtn = (TextView) findViewById(R.id.btnGoBack);
        goBackBtn.setOnClickListener(this);

        btn_check = (Button) findViewById(R.id.check_btn);
        btn_check.setOnClickListener(this);

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

        autoComplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        autoComplete.setAdapter(adapter);

        getImage();
    }

    public void getImage()
    {
        mImageId.add(R.drawable.a);
        mNames.add("a");
        mImageId.add(R.drawable.ba);
        mNames.add("ba");
        mImageId.add(R.drawable.ka);
        mNames.add("ka");
        mImageId.add(R.drawable.da);
        mNames.add("da");
        mImageId.add(R.drawable.e);
        mNames.add("e");
        mImageId.add(R.drawable.ga);
        mNames.add("ga");
        mImageId.add(R.drawable.ha);
        mNames.add("ha");
        mImageId.add(R.drawable.i);
        mNames.add("i");
        mImageId.add(R.drawable.la);
        mNames.add("la");
        mImageId.add(R.drawable.ma);
        mNames.add("ma");
        mImageId.add(R.drawable.na);
        mNames.add("na");
        mImageId.add(R.drawable.nga);
        mNames.add("nga");
        mImageId.add(R.drawable.o);
        mNames.add("o");
        mImageId.add(R.drawable.pa);
        mNames.add("pa");
        mImageId.add(R.drawable.ra);
        mNames.add("ra");
        mImageId.add(R.drawable.sa);
        mNames.add("sa");
        mImageId.add(R.drawable.ta);
        mNames.add("ta");
        mImageId.add(R.drawable.u);
        mNames.add("u");
        mImageId.add(R.drawable.wa);
        mNames.add("wa");
        mImageId.add(R.drawable.ya);
        mNames.add("ya");

    }

    public void initRecyclerView(ArrayList<String> nNsmes, ArrayList<Integer> mId)
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleViewer);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter recyclerAdapter = new recyclerViewAdapter(nNsmes, mId,getApplicationContext());
        recyclerView.setAdapter(recyclerAdapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGoBack:
                finish();
                break;
            case R.id.check_btn:
                convertToImage(autoComplete.getText().toString());
                break;
        }
    }

    public void convertToImage(String text){
        String[] vowels = {"a", "e", "i", "u", "o"};
        String[] consonants = {"b", "k", "d", "g", "h", "l", "m", "n", "ng", "p", "r", "s", "t", "w", "y"};

        String[] namesArray = mNames.toArray(new String[0]); // Convert ArrayList to array

        int id = 0;
        ArrayList<String> chop = new ArrayList<String>();
        ArrayList<Integer> mId = new ArrayList<Integer>();
        while (id < text.length()){
            if (id == text.length() - 1)
            {
                String word = String.valueOf(text.charAt(id));
                int index = Arrays.asList(namesArray).indexOf(word);
                chop.add(word);
                mId.add(mImageId.get(index));
                break;
            }

            if (Arrays.asList(vowels).contains(String.valueOf(text.charAt(id))) && id == 0) {
                String word = String.valueOf(text.charAt(id));
                int index = Arrays.asList(namesArray).indexOf(word);
                chop.add(word);
                mId.add(mImageId.get(index));
            }
            else if (Arrays.asList(consonants).contains(String.valueOf(text.charAt(id))) && id == 0)
            {
                String word = String.valueOf(text.charAt(id)) + String.valueOf(text.charAt(id+1));
                int index = Arrays.asList(namesArray).indexOf(word);
                chop.add(word);
                mId.add(mImageId.get(index));
                id++;
            }
            else if (text.charAt(id) == 'n'  && text.charAt(id + 1) == 'g')
            {
                String word = String.valueOf(text.charAt(id)) + String.valueOf(text.charAt(id+1) +
                        String.valueOf(text.charAt(id+2)));
                int index = Arrays.asList(namesArray).indexOf(word);
                chop.add(word);
                mId.add(mImageId.get(index));
                id += 2;
            }
            else if (Arrays.asList(vowels).contains(String.valueOf(text.charAt(id-1))) && Arrays.asList(vowels).contains(String.valueOf(text.charAt(id))))
            {
                String word = String.valueOf(text.charAt(id));
                int index = Arrays.asList(namesArray).indexOf(word);
                chop.add(word);
                mId.add(mImageId.get(index));
            }
            else if(Arrays.asList(consonants).contains(String.valueOf(text.charAt(id))) && Arrays.asList(vowels).contains(String.valueOf(text.charAt(id + 1))) )
            {
                String word = String.valueOf(text.charAt(id)) + String.valueOf(text.charAt(id+1));
                int index = Arrays.asList(namesArray).indexOf(word);
                chop.add(word);
                mId.add(mImageId.get(index));
                id++;
            }
            id++;
        }
        initRecyclerView(chop, mId);
    }

    public void toastSomething(ArrayList<String> chop)
    {
        for (String s: chop)
        {
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
    }
}