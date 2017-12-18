package com.dmtec.a91jiasu.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.common.CustomSharePreference;
import com.dmtec.a91jiasu.common.CustomToast;
import com.dmtec.a91jiasu.logic.BaseActivity;
import com.dmtec.a91jiasu.logic.MessageAdapter;
import com.dmtec.a91jiasu.models.JPushMessage;

import java.util.ArrayList;
import java.util.Collections;

public class SysMsgActivity extends BaseActivity implements View.OnClickListener, MessageAdapter.OnRecyclerViewListener{

    private View back;
    private TextView title,noMsgView;
    private RecyclerView recyclerView;

    private ArrayList<JPushMessage> msgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_msg);

        msgs = CustomSharePreference.getJPushMessages(getApplicationContext());
        Collections.reverse(msgs);
        initView();
    }


    private void initView(){
        back = findViewById(R.id.toolbar_back);
        back.setOnClickListener(this);

        title = (TextView)findViewById(R.id.title);
        title.setText("历史消息");

        noMsgView = (TextView)findViewById(R.id.no_msg_tv);
        if (msgs.size() > 0){
            noMsgView.setVisibility(View.GONE);
        }else{
            noMsgView.setVisibility(View.VISIBLE);
        }

        initList();
    }

    private void initList(){
        recyclerView = (RecyclerView)findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        MessageAdapter adapter = new MessageAdapter(getApplicationContext(),msgs);
        adapter.setOnRecyclerViewListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        JPushMessage message = msgs.get(position);
        String url = message.getUrl();
        Intent intent = new Intent(SysMsgActivity.this,WebActivity.class);
        intent.putExtra("url",url);
        if(!url.equals("")){
            startActivity(intent);
        }
    }
}
