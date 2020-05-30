package com.hon454.matchup;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hon454.matchup.Database.Comment;
import com.hon454.matchup.Database.Post;
import com.hon454.matchup.Database.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostDetailActivity extends BaseActivity {
    private static final String TAG = "PostDetailActivity";
    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private Post mPost;
    private CommentAdapter mAdapter;

    private TextView mTitleView;
    private TextView mAuthorView;
    private TextView mCountdownView;

    private ImageView mThumbnailView;

    private TextView mLeftOptionModifierView;
    private TextView mLeftOptionTitleView;
    private TextView mLeftOptionPercentageView;
    private Button mLeftVoteButton;

    private TextView mRightOptionModifierView;
    private TextView mRightOptionTitleView;
    private TextView mRightOptionPercentageView;
    private Button mRightVoteButton;

    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }


        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts").child(mPostKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference().child("post-comments").child(mPostKey);

        // Initialize View
        mAuthorView = findViewById(R.id.postAuthor);
        mTitleView = findViewById(R.id.postTitle);
        mCountdownView = findViewById(R.id.postCountdown);

        mThumbnailView = findViewById(R.id.postThumbnail);

        mLeftOptionModifierView  = findViewById(R.id.postLeftOptionModifier);
        mLeftOptionTitleView = findViewById(R.id.postLeftOptionTitle);
        mLeftOptionPercentageView = findViewById(R.id.postLeftOptionPercentage);
        mLeftVoteButton = findViewById(R.id.leftVoteButton);

        mRightOptionModifierView = findViewById(R.id.postRightOptionModifier);
        mRightOptionTitleView = findViewById(R.id.postRightOptionTitle);
        mRightOptionPercentageView = findViewById(R.id.postRightOptionPercentage);
        mRightVoteButton = findViewById(R.id.rightVoteButton);

        mCommentField = findViewById(R.id.fieldCommentText);
        mCommentButton = findViewById(R.id.buttonPostComment);
        mCommentsRecycler = findViewById(R.id.recyclerPostComments);

        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

        mLeftVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteLeft();
            }
        });
        mRightVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteRight();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                mAuthorView.setText(post.authorName);
                mTitleView.setText(post.title);
//                mCountdownView.setText();

                LoadThumbnailWithGlide(post.thumbnailDownloadUrl);

                mLeftOptionModifierView.setText(post.leftModifier);
                mLeftOptionTitleView.setText(post.leftTitle);

                mRightOptionModifierView.setText(post.rightModifier);
                mRightOptionTitleView.setText(post.rightTitle);

                updateVoteRate(post.leftVoterUidList.size(), post.rightVoterUidList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(PostDetailActivity.this, "Failed to load post.", Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        mPostReference.addValueEventListener(postListener);
        mPostListener = postListener;

        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }

        mAdapter.cleanupListener();
    }

    private void LoadThumbnailWithGlide(String thumbnailDownloadUrl) {
        Glide.with(this)
                .load(thumbnailDownloadUrl)
                .centerCrop()
                .into(mThumbnailView);
    }

    private void voteRight() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if(post.leftVoterUidList.contains(getUid()) || post.rightVoterUidList.contains(getUid())) {
                    Toast.makeText(PostDetailActivity.this, "이미 투표에 참가하셨습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    post.rightVoterUidList.add(getUid());

                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    database.child("posts").child(post.uid).child("rightVoterUidList").setValue(post.rightVoterUidList);
                    database.child("user-posts").child(post.authorUid).child(post.uid).child("rightVoterUidList").setValue(post.rightVoterUidList);
                    Toast.makeText(PostDetailActivity.this, "투표 참여 감사합니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(PostDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mPostReference.addListenerForSingleValueEvent(postListener);
    }

    private void voteLeft() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if(post.leftVoterUidList.contains(getUid()) || post.leftVoterUidList.contains(getUid())) {
                    Toast.makeText(PostDetailActivity.this, "이미 투표에 참가하셨습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    post.leftVoterUidList.add(getUid());

                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    database.child("posts").child(post.uid).child("leftVoterUidList").setValue(post.leftVoterUidList);
                    database.child("user-posts").child(post.authorUid).child(post.uid).child("leftVoterUidList").setValue(post.leftVoterUidList);
                    Toast.makeText(PostDetailActivity.this, "투표 참여 감사합니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(PostDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mPostReference.addListenerForSingleValueEvent(postListener);
    }

    private void updateVoteRate(int leftVotersNumber, int rightVotersNumber) {
        int allVotersNumber = leftVotersNumber + rightVotersNumber;
        if(allVotersNumber == 0) {
            mLeftOptionPercentageView.setText("50%");
            mRightOptionPercentageView.setText("50%");
        } else {
            if(leftVotersNumber == 0 ) {
                mLeftOptionPercentageView.setText("0%");
            } else {
                mLeftOptionPercentageView.setText(String.format("%.1f%%", (float)leftVotersNumber / allVotersNumber * 100));
            }

            if(rightVotersNumber == 0 ) {
                mRightOptionPercentageView.setText("0%");
            } else {
                mRightOptionPercentageView.setText(String.format("%.1f%%", (float)rightVotersNumber / allVotersNumber * 100));
            }
        }
    }

    private void postComment() {
        final String uid = getUid();
        getDatabaseReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.nickname;

                        String commentText = mCommentField.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        mCommentsReference.push().setValue(comment);

                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = itemView.findViewById(R.id.commentAuthor);
            bodyView = itemView.findViewById(R.id.commentBody);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    Comment comment = dataSnapshot.getValue(Comment.class);

                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        mComments.set(commentIndex, newComment);

                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }
    }
}
