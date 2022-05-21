package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SubActivity extends AppCompatActivity {

    private TextView tv_sub;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        list = (ListView)findViewById(R.id.list);
        tv_sub = findViewById(R.id.tv_sub);

        List<String> data = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        list.setAdapter(adapter);

        data.add("넣고싶은 데이터"); // 여러개도 상관 없음 -> 리스트형으로 데이터 전달
        data.add("이것도 넣어봐야지");
        adapter.notifyDataSetChanged();

        Intent intent = getIntent();
        String str = intent.getStringExtra("str"); // get str from MainActivity

        tv_sub.setText(str);
    }
}