package com.hon454.matchup.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hon454.matchup.Database.Post;
import com.hon454.matchup.PostDetailActivity;
import com.hon454.matchup.R;

import java.util.ArrayList;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {
    private ArrayList<Post> postList;
    private Context context;

    public MyPostAdapter(ArrayList<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mypost, parent, false);
        MyPostAdapter.ViewHolder viewHolder = new MyPostAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostAdapter.ViewHolder viewHolder, int position) {
        final String postKey = postList.get(position).uid;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch PostDetailActivity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                context.startActivity(intent);
            }
        });

        Glide.with(viewHolder.itemView)
                .load(postList.get(position).getThumbnailDownloadUrl())
                .centerCrop()
                .into(viewHolder.iv_mypostThumnail);
        viewHolder.tv_mypostTitle.setText(postList.get(position).getTitle());
        viewHolder.tv_mypostLeftTitle.setText(postList.get(position).getLeftTitle());
        viewHolder.tv_mypostRightTitle.setText(postList.get(position).getRightTitle());
        int leftVotersNumber = postList.get(position).leftVoterUidList.size();
        int rightVotersNumber = postList.get(position).rightVoterUidList.size();
        int allVotersNumber = leftVotersNumber + rightVotersNumber;
        if(allVotersNumber == 0) {
            viewHolder.tv_mypostLeftOptionPercentage.setText("50%");
            viewHolder.tv_mypostRightOptionPercentage.setText("50%");
        } else {
            viewHolder.tv_mypostLeftOptionPercentage.setText(String.format("%.1f%%", (float)leftVotersNumber / allVotersNumber * 100));
            viewHolder.tv_mypostRightOptionPercentage.setText(String.format("%.1f%%", (float)rightVotersNumber / allVotersNumber * 100));
        }
    }

    @Override
    public int getItemCount() {
        return (postList != null ? postList.size() : 0 );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_mypostThumnail;
        TextView tv_mypostTitle, tv_mypostLeftTitle, tv_mypostRightTitle, tv_mypostLeftOptionPercentage, tv_mypostRightOptionPercentage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_mypostThumnail = itemView.findViewById(R.id.iv_mypostThumnail);
            tv_mypostTitle = itemView.findViewById(R.id.tv_mypostTitle);
            tv_mypostLeftTitle = itemView.findViewById(R.id.tv_mypostLeftTitle);
            tv_mypostRightTitle = itemView.findViewById(R.id.tv_mypostRightTitle);
            tv_mypostLeftOptionPercentage = itemView.findViewById(R.id.tv_mypostLeftOptionPercentage);
            tv_mypostRightOptionPercentage = itemView.findViewById(R.id.tv_mypostRightOptionPercentage);
        }
    }

}
