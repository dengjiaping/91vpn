package com.dmtec.a91jiasu.logic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.models.JPushMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmtec on 2017/10/9.
 *
 */

public class MessageAdapter extends RecyclerView.Adapter {

    public static interface OnRecyclerViewListener{
        void onItemClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;
    private static final String TAG = MessageAdapter.class.getSimpleName();
    private ArrayList<JPushMessage> list;
    private Context context;

    public MessageAdapter(Context context, ArrayList<JPushMessage> arrayList){
        this.list = arrayList;
        this.context = context;
    }

    public void setOnRecyclerViewListener(OnRecyclerViewListener listener){
        this.onRecyclerViewListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int[] colors = {context.getResources().getColor(R.color.list_color_0), context.getResources().getColor(R.color.list_color_1), context.getResources().getColor(R.color.list_color_2)};
        MessageViewHolder messageViewHolder = (MessageViewHolder)holder;
        messageViewHolder.position = position;
        JPushMessage message =list.get(position);
        messageViewHolder.title.setText(message.getTitle()+"");
        messageViewHolder.content.setText(message.getContent()+"");
        messageViewHolder.date.setText(message.getDate()+"");
        GradientDrawable gd = (GradientDrawable)(messageViewHolder.btn).getBackground();
        gd.setColor(colors[position%colors.length]);
        messageViewHolder.btn.setTextColor(Color.WHITE);
        messageViewHolder.btn.setText(message.getTitle().substring(0,2));
        messageViewHolder.btn.setTextSize(16);
        messageViewHolder.btn.setClickable(false);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        view.setLayoutParams(lp);
        return new MessageViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private View rootView;
        public TextView title,content,date;
        private int position;
        private TextView btn;

        private MessageViewHolder(View itemView){
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            content = (TextView)itemView.findViewById(R.id.content);
            date = (TextView)itemView.findViewById(R.id.date);
            btn = (TextView)itemView.findViewById(R.id.btn);
            rootView = itemView.findViewById(R.id.root);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(null != onRecyclerViewListener){
                onRecyclerViewListener.onItemClick(position);
            }
        }
    }
}
