package com.hon454.matchup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hon454.matchup.Database.Post;
import com.hon454.matchup.Database.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;

    private EditText mTitleField;
    private EditText mLeftOptionModifierField;
    private EditText mLeftOptionTitleField;
    private EditText mRightOptionModifierField;
    private EditText mRightOptionTitleField;


    private ImageView mThumbnailImageButton;
    private Uri thumbnailUri;

    private ImageView iv_newPost_to_main;
    private TextView tv_submit;

    private boolean isPosting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mTitleField = findViewById(R.id.et_title);
        mLeftOptionModifierField = findViewById(R.id.et_leftOptionModifier);
        mLeftOptionTitleField = findViewById(R.id.et_leftOptionTitle);
        mRightOptionModifierField = findViewById(R.id.et_rightOptionModifier);
        mRightOptionTitleField = findViewById(R.id.et_rightOptionTitle);

        //뒤로가기 버튼
        iv_newPost_to_main = findViewById(R.id.iv_newPost_to_main);
        iv_newPost_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //완료 버튼
        tv_submit = findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPosting)
                {
                    return;
                }
                submitPost();
            }
        });

        //썸네일 이미지 선택하기
        mThumbnailImageButton = (ImageView) findViewById(R.id.imageButton_thumbnail);
        mThumbnailImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK) {
            thumbnailUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), thumbnailUri);
                mThumbnailImageButton.setImageBitmap(bitmap);
                mThumbnailImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String leftOptionModifier = mLeftOptionModifierField.getText().toString();
        final String leftOptionTitle = mLeftOptionTitleField.getText().toString();
        final String rightOptionModifier = mRightOptionModifierField.getText().toString();
        final String rightOptionTitle = mRightOptionTitleField.getText().toString();
        Spinner spPostSubject = (Spinner)findViewById(R.id.spinner_post_subject);
        final String postSubject = (String)spPostSubject.getSelectedItem();

        if(thumbnailUri == null) {
            Toast.makeText(NewPostActivity.this, "썸네일 이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        if(TextUtils.isEmpty(leftOptionModifier)) {
            mLeftOptionModifierField.setError(REQUIRED);
            return;
        }

        if(TextUtils.isEmpty(leftOptionTitle)) {
            mLeftOptionTitleField.setError(REQUIRED);
            return;
        }

        if(TextUtils.isEmpty(rightOptionModifier)) {
            mRightOptionModifierField.setError(REQUIRED);
            return;
        }

        if(TextUtils.isEmpty(rightOptionTitle)) {
            mRightOptionTitleField.setError(REQUIRED);
            return;
        }

        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser.getUid();

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewPost(userId, user.getNickname(), title, leftOptionModifier,
                                    leftOptionTitle, rightOptionModifier, rightOptionTitle, postSubject, thumbnailUri);
                        }

                        setEditingEnabled(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mLeftOptionModifierField.setEnabled(enabled);
        mLeftOptionTitleField.setEnabled(enabled);
        mRightOptionModifierField.setEnabled(enabled);
        mRightOptionTitleField.setEnabled(enabled);

        isPosting = !enabled;
    }

    private void writeNewPost(final String authorUid, final String authorName, final String title, final String leftOptionModifier,
                              final String leftOptionTitle, final String rightOptionModifier, final String rightOptionTitle, final String subject, Uri thumbnailUri) {

        final String postUid = mDatabase.child("posts").push().getKey();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String filename = postUid + ".png";


        final StorageReference storageReference = storage.getReferenceFromUrl("gs://matchup-7ce60.appspot.com").child("images/thumbnail/" + filename);

        // 썸네일 업로드
        storageReference.putFile(thumbnailUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // 원격 저장소의 썸네일 다운로드 URL 가져오기

                        Post post = new Post(postUid, authorUid, authorName, title, leftOptionModifier,
                                leftOptionTitle, rightOptionModifier, rightOptionTitle, subject, null);

                        UploadPost(storageReference, post);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void UploadPost(StorageReference storageReference, final Post post) {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                post.thumbnailDownloadUrl = uri.toString();
                Map<String, Object> postValues = post.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/posts/" + post.uid, postValues);
                childUpdates.put("/user-posts/" + post.authorUid + "/" + post.uid, postValues);
                mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Intent intent = new Intent();
                        intent.putExtra("result", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

//    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.basic_right_eye);  // first image
//    Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.basic_right_eye);  // second image
//    Bitmap[] listBmp= {bitmap1, bitmap2};
//    Bitmap mergedImg= mergeMultiple(listBmp);
//
//    private Bitmap mergeMultiple(Bitmap[] parts){
//
//        Bitmap result = Bitmap.createBitmap(parts[0].getWidth() * 2, parts[0].getHeight() * 2, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(result);
//        Paint paint = new Paint();
//        for (int i = 0; i < parts.length; i++) {
//            canvas.drawBitmap(parts[i], parts[i].getWidth() * (i % 2), parts[i].getHeight() * (i / 2), paint);
//        }
//        return result;
//    }
}


