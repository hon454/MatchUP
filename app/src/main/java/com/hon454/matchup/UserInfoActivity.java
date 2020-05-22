package com.hon454.matchup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hon454.matchup.Database.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserInfoActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Uri filePath;
    ImageButton btn_profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();

        Button btn_save = (Button)findViewById(R.id.btn_save_info);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfile();
                userInfoSave();
            }
        });

        //앨범에서 profile 설정하기
        btn_profile = (ImageButton)findViewById(R.id.imageButton_profile);
        btn_profile.setOnClickListener(new View.OnClickListener() {
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
        //request code가 0이고 ok를 선택했고 data에 뭔가 들어있다면
        if(requestCode == 0 && resultCode == RESULT_OK) {
            filePath = data.getData();
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                btn_profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //서버에 저장할 profile 사진 설정하기
    private void uploadProfile() {
        //업로드할 파일이 있으면 수행
        if(filePath != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //파일명 : 20200514_0000
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".png";

            StorageReference storageRef = storage.getReferenceFromUrl("gs://matchup-7ce60.appspot.com").child("images/" + filename);
            storageRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    private void userInfoSave() {
        String nickname = ((EditText)findViewById(R.id.et_nickname)).getText().toString();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String currYear = yearFormat.format(currentTime);
        String birthYear = ((EditText)findViewById(R.id.et_birthYear)).getText().toString();
        String age = Integer.toString(Integer.parseInt(currYear) - Integer.parseInt(birthYear) + 1);

        Spinner spinner_jobs = (Spinner)findViewById(R.id.spinner_jobs);
        String job = (String)spinner_jobs.getSelectedItem();
        spinner_jobs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Spinner spinner_residence = (Spinner)findViewById(R.id.spinner_residences);
        String residence = (String)spinner_residence.getSelectedItem();
        spinner_residence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //RadioButton btn_male = (RadioButton)findViewById(R.id.btn_male);
        //RadioButton btn_femail = (RadioButton)findViewById(R.id.btn_femail);
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup_sex);
        RadioButton selectedBtn = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
        String sex = selectedBtn.getText().toString();
        // Write new user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        writeNewUser(user.getUid(), filePath, nickname, age, sex, job, residence);

        // Go to MainActivity
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

    private void writeNewUser(String userId, Uri uri, String nickname, String age, String sex, String job, String residence) {
        String profileUri = "";
        if(filePath != null) {
            profileUri = filePath.toString();
        }

        User user = new User(profileUri, nickname, age, sex, job, residence);

        mDatabase.child("users").child(userId).setValue(user);
    }
}
