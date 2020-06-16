package com.hon454.matchup;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hon454.matchup.Database.Comment;
import com.hon454.matchup.Database.Post;
import com.hon454.matchup.Database.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private ImageView mProfileView;

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

    private ImageButton mBackButton;

    private ProgressBar mStatsProgressBar;
    private TextView mStatsLeftOptionPercentageTextView;
    private TextView mStatsRightOptionPercentageTextView;

    private Spinner mAgeSpinner;
    private Spinner mSexSpinner;
    private Spinner mJobSpinner;
    private Spinner mResidenceSpinner;

    private String mSelectedAge = "전연령";
    private String mSelectedSex = "모든 성별";
    private String mSelectedJob = "모든 직종";
    private String mSelectedResidence = "모든 지역";

    private boolean isInitialized = false;

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
        mProfileView = findViewById(R.id.postAuthorProfile);

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

        mStatsProgressBar = findViewById(R.id.statsProgressBar);
        mStatsLeftOptionPercentageTextView = findViewById(R.id.statsLeftOptionPercentage);
        mStatsRightOptionPercentageTextView = findViewById(R.id.statsRightOptionPercentage);

        setSpinner();

        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
        //댓글 recyclerView 스크롤 기능 삭제
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

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

        mBackButton = findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        isInitialized = true;
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPost = dataSnapshot.getValue(Post.class);

                DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(mPost.authorUid);
                ValueEventListener userListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User mUser = dataSnapshot.getValue(User.class);
                        Glide.with(getApplicationContext())
                                .load(mUser.getProfileUri())
                                .centerCrop()
                                .into(mProfileView);
                        mProfileView.setBackground(new ShapeDrawable(new OvalShape()));
                        mProfileView.setClipToOutline(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                mUserReference.addValueEventListener(userListener);

                mAuthorView.setText(mPost.authorName);
                mTitleView.setText(mPost.title);
                SimpleDateFormat dayTime = new SimpleDateFormat("dd일 hh:mm:ss");
                String curTime = dayTime.format(new Date(mPost.createdDateTime));
                mCountdownView.setText(curTime);
//                mCountdownView.setText();

                LoadThumbnailWithGlide(mPost.thumbnailDownloadUrl);

                mLeftOptionModifierView.setText(mPost.leftModifier);
                mLeftOptionTitleView.setText(mPost.leftTitle);

                mRightOptionModifierView.setText(mPost.rightModifier);
                mRightOptionTitleView.setText(mPost.rightTitle);

                updateVoteRate(mPost.leftVoterUidList.size(), mPost.rightVoterUidList.size());
                updateStats();
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
        mCommentsRecycler.setNestedScrollingEnabled(false);
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
                mPost = dataSnapshot.getValue(Post.class);
                if(mPost.leftVoterUidList.contains(getUid()) || mPost.rightVoterUidList.contains(getUid())) {
                    Toast.makeText(PostDetailActivity.this, "이미 투표에 참가하셨습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    mPost.rightVoterUidList.add(getUid());

                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    database.child("posts").child(mPost.uid).child("rightVoterUidList").setValue(mPost.rightVoterUidList);
                    database.child("user-posts").child(mPost.authorUid).child(mPost.uid).child("rightVoterUidList").setValue(mPost.rightVoterUidList);
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
                mPost = dataSnapshot.getValue(Post.class);
                if(mPost.leftVoterUidList.contains(getUid()) || mPost.rightVoterUidList.contains(getUid())) {
                    Toast.makeText(PostDetailActivity.this, "이미 투표에 참가하셨습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    mPost.leftVoterUidList.add(getUid());

                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    database.child("posts").child(mPost.uid).child("leftVoterUidList").setValue(mPost.leftVoterUidList);
                    database.child("user-posts").child(mPost.authorUid).child(mPost.uid).child("leftVoterUidList").setValue(mPost.leftVoterUidList);
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

    private int getAllVotersNumber() {
        return mPost.leftVoterUidList.size() + mPost.rightVoterUidList.size();
    }

    private float getLeftVotersPercentage() {
        assert (mPost != null) : "post must not be null";

        float allVotersNumber = getAllVotersNumber();

        if(allVotersNumber == 0) {
            return 50f;
        }

        return mPost.leftVoterUidList.size() / allVotersNumber * 100f;
    }

    private float getRightVotersPercentage() {
        assert (mPost != null) : "post must not be null";

        float allVotersNumber = getAllVotersNumber();

        if(allVotersNumber == 0) {
            return 50f;
        }

        return mPost.rightVoterUidList.size() / allVotersNumber * 100f;
    }

    private void updateStats() {

        if(!isInitialized) {
            return;
        }

        assert (mPost != null) : "post must not be null";

        final ArrayList<User> leftVoterList = new ArrayList<>();
        final ArrayList<User> rightVoterList = new ArrayList<>();

        DatabaseReference userDatabaseReference = getUserDatabaseReference();
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                leftVoterList.clear();
                rightVoterList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    String userUid = user.getUid();
                    Log.d("PostDetailActivity3", userUid);
                    if(mPost.leftVoterUidList.contains(userUid)) {
                        leftVoterList.add(user);
                    }
                    else if(mPost.rightVoterUidList.contains(userUid)) {
                        rightVoterList.add(user);
                    }
                }

                final ArrayList<User> filteredLeftVoterList = getFilteredVoterList(leftVoterList);
                final ArrayList<User> filteredRightVoterList = getFilteredVoterList(rightVoterList);

                final int filteredLeftVotersSize = filteredLeftVoterList.size();
                final int filteredRightVotersSize = filteredRightVoterList.size();
                Log.d("PostDetailActivity1", leftVoterList.size() + "/" + rightVoterList.size());
                Log.d("PostDetailActivity2", filteredLeftVotersSize + "/" + filteredRightVotersSize);

                final int filteredAllVotersSize = filteredLeftVotersSize + filteredRightVotersSize;

                float filteredLeftVotersPercentage;
                float filteredRightVotersPercentage;

                if(filteredAllVotersSize == 0) {
                    filteredLeftVotersPercentage = 0f;
                    filteredRightVotersPercentage = 100f;
                    mStatsProgressBar.setSecondaryProgress(100);
                } else {
                    filteredLeftVotersPercentage = (float)filteredLeftVotersSize / filteredAllVotersSize * 100f;
                    filteredRightVotersPercentage = (float)filteredRightVotersSize / filteredAllVotersSize * 100f;
                    mStatsProgressBar.setSecondaryProgress(0);
                }

                if(filteredLeftVotersPercentage < 5f) {
                    mStatsLeftOptionPercentageTextView.setText("");

                    if(filteredAllVotersSize == 0) {
                        mStatsRightOptionPercentageTextView.setText("해당 조건을 만족하는 투표가 없습니다.");
                    } else {
                        mStatsRightOptionPercentageTextView.setText(String.format("%.1f%%", filteredRightVotersPercentage));
                    }
                }
                else if(filteredRightVotersPercentage < 5f) {
                    mStatsLeftOptionPercentageTextView.setText(String.format("%.1f%%", filteredLeftVotersPercentage));
                    mStatsRightOptionPercentageTextView.setText("");
                }
                else {
                    mStatsLeftOptionPercentageTextView.setText(String.format("%.1f%%", filteredLeftVotersPercentage));
                    mStatsRightOptionPercentageTextView.setText(String.format("%.1f%%", filteredRightVotersPercentage));
                }

                mStatsLeftOptionPercentageTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, filteredLeftVotersPercentage));
                mStatsRightOptionPercentageTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, filteredRightVotersPercentage));
                mStatsProgressBar.setProgress((int)filteredLeftVotersPercentage, true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private ArrayList<User> getFilteredVoterList(ArrayList<User> voterList) {
        ArrayList<User> filteredVoterList = new ArrayList<>();

        if(voterList == null || voterList.isEmpty()) {
            return filteredVoterList;
        }

        for(User voter : voterList) {
            int age = Integer.parseInt(voter.getAge());
            switch (mSelectedAge) {
                case "전연령":
                    break;
                case "20세 미만":
                    if(!(age < 20)) {
                        continue;
                    }
                    break;
                case "20대":
                    if(!(20 <= age && age < 30)) {
                        continue;
                    }
                    break;
                case "30대":
                    if(!(30 <= age && age < 40)) {
                        continue;
                    }
                    break;
                case "40대":
                    if(!(40 <= age && age < 50)) {
                        continue;
                    }
                    break;
                case "50대":
                    if(!(50 <= age && age < 60)) {
                        continue;
                    }
                    break;
                case "60대 이상":
                    if(!(60 <= age)) {
                        continue;
                    }
                    break;
            }

            String sex = voter.getSex();
            if(!mSelectedSex.equals("모든 성별")) {
                if(!sex.equals(mSelectedSex)) {
                    continue;
                }
            }

            String job = voter.getJobs();
            if(!mSelectedJob.equals("모든 직종")) {
                if(!job.equals(mSelectedJob)) {
                    continue;
                }
            }

            String residence = voter.getResidence();
            if(!mSelectedResidence.equals("모든 지역")) {
                if(!residence.equals(mSelectedResidence)) {
                    continue;
                }
            }
            filteredVoterList.add(voter);
        }

        return filteredVoterList;
    }

    private void updateStatsUi() {

    }

    private void postComment() {
        final String authorUid = getUid();
        getDatabaseReference().child("users").child(authorUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.getNickname();

                        String commentUid = mCommentsReference.push().getKey();

                        String commentText = mCommentField.getText().toString();
                        Comment comment = new Comment(commentUid, authorUid, authorName, commentText);

                        mCommentsReference.child(commentUid).setValue(comment);
                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void setSpinner() {
        setAgeSpinner();
        setSexSpinner();
        setJobSpinner();
        setResidenceSpinner();
    }

    private void setAgeSpinner() {
        mAgeSpinner = findViewById(R.id.ageSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.age_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);;
        mAgeSpinner.setAdapter(adapter);
        mAgeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedAge = parent.getItemAtPosition(position).toString();
                updateStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSexSpinner() {
        mSexSpinner = findViewById(R.id.sexSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sex_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);;
        mSexSpinner.setAdapter(adapter);
        mSexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedSex = parent.getItemAtPosition(position).toString();
                updateStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setJobSpinner() {
        mJobSpinner = findViewById(R.id.jobSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.job_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);;
        mJobSpinner.setAdapter(adapter);
        mJobSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedJob = parent.getItemAtPosition(position).toString();
                updateStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setResidenceSpinner() {
        mResidenceSpinner = findViewById(R.id.residenceSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.residence_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);;
        mResidenceSpinner.setAdapter(adapter);
        mResidenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedResidence = parent.getItemAtPosition(position).toString();
                updateStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView authorView;
        public TextView bodyView;
        public ImageButton removeCommentButton;
        public ImageView commentView;


        public CommentViewHolder(View itemView) {
            super(itemView);

            commentView = itemView.findViewById(R.id.commentPhoto);
            authorView = itemView.findViewById(R.id.commentAuthor);
            bodyView = itemView.findViewById(R.id.commentBody);
            removeCommentButton = itemView.findViewById(R.id.removeCommentButton);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;
        private DatabaseReference mUserReference;

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
        public void onBindViewHolder(final CommentViewHolder holder, int position) {
            final Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);

            mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(comment.authorUid);
            ValueEventListener userLinstener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Glide.with(holder.itemView)
                            .load(user.getProfileUri())
                            .centerCrop()
                            .into(holder.commentView);
                    holder.commentView.setBackground(new ShapeDrawable(new OvalShape()));
                    holder.commentView.setClipToOutline(true);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mUserReference.addValueEventListener(userLinstener);


            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d(TAG, comment.authorUid + "/" + uid);
            if(comment.authorUid.equals(uid)) {
                holder.removeCommentButton.setVisibility(View.VISIBLE);
                holder.removeCommentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
                        ad.setTitle("댓글 삭제");
                        ad.setMessage("댓글을 삭제하시겠습니까?");
                        ad.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeComment(comment.uid);
                                dialog.dismiss();
                            }
                        });

                        ad.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        ad.show();
                    }
                });
            } else {
                holder.removeCommentButton.setVisibility(View.INVISIBLE);
            }
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

        public void removeComment(final String commentUid) {
            mDatabaseReference.child(commentUid).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    Toast.makeText(mContext, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Complete remove comment " + commentUid);
                }
            });
        }
    }
}
