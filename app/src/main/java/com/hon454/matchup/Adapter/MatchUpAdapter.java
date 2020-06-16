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

public class MatchUpAdapter extends RecyclerView.Adapter<MatchUpAdapter.ViewHolder> {
    private ArrayList<Post> postList;
    private Context context;

    public MatchUpAdapter(ArrayList<Post> arrayList, Context context) {
        this.postList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchUpAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matchup, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchUpAdapter.ViewHolder viewHolder, int position) {
        // Glide는 이미지 크기가 무거울때 알아서 잘 조절해서 업로드해줍니다!

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
                .into(viewHolder.iv_Post);
        viewHolder.tv_postTitle.setText(postList.get(position).getTitle());
        viewHolder.tv_leftTitle.setText(postList.get(position).getLeftTitle());
        viewHolder.tv_rightTitle.setText(postList.get(position).getRightTitle());
    }

    @Override
    public int getItemCount() {
        return (postList != null ? postList.size() : 0 );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_Post;
        TextView tv_postTitle, tv_leftTitle, tv_rightTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_Post = itemView.findViewById(R.id.iv_postImage);
            tv_postTitle = itemView.findViewById(R.id.tv_postTitle);
            tv_leftTitle = itemView.findViewById(R.id.tv_leftTitle);
            tv_rightTitle = itemView.findViewById(R.id.tv_rightTitle);

        }
    }
}
