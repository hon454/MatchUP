package com.hon454.matchup;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hon454.matchup.Database.User;

import java.util.Calendar;

public class EditUserInfoActivity extends BaseActivity {

    private DatabaseReference mUserReference;

    private Button editInfo;
    private EditText mNickname;
    private EditText mBirthYear;
    private ImageView mProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        mNickname = (EditText)findViewById(R.id.et_edit_nickname);
        mBirthYear = (EditText)findViewById(R.id.et_edit_birthYear);
        mProfile = (ImageView)findViewById(R.id.iv_edit_profile);
        editInfo = (Button)findViewById(R.id.btn_edit_info);
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(getUid());
    }

    @Override
    protected void onStart() {
        super.onStart();

        ValueEventListener userLinstener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mNickname.setText(user.getNickname());
                Calendar cal = Calendar.getInstance();
                int curYear = cal.get(Calendar.YEAR);
                mBirthYear.setText(String.valueOf(curYear - Integer.parseInt(user.getAge()) + 1));
                Glide.with(getApplicationContext())
                        .load(user.getProfileUri())
                        .centerCrop()
                        .into(mProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mUserReference.addValueEventListener(userLinstener);
    }
}
