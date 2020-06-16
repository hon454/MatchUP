package com.hon454.matchup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
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
import com.hon454.matchup.Database.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditUserInfoActivity extends BaseActivity {

    private DatabaseReference mUserReference;
    private Uri album_uri;

    private Button editInfo;
    private Button btn_back;
    private ImageView iv_edit_profile;
    private EditText mNickname;
    private EditText mBirthYear;
    private ImageView mProfile;
    private Spinner spinner_jobs;
    private Spinner spinner_residence;
    private RadioButton radioButton_male;
    private RadioButton radioButton_female;
    private RadioGroup radioGroup;

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
                if(album_uri != null){
                    userInfoSave();
                    Toast.makeText(getApplicationContext(),"회원정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"수정할 사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_back = (Button)findViewById(R.id.btn_cancle_edit);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_edit_profile = (ImageView)findViewById(R.id.iv_edit_profile);
        iv_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(getUid());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request code가 0이고 ok를 선택했고 data에 뭔가 들어있다면
        if(requestCode == 0 && resultCode == RESULT_OK) {
            album_uri = data.getData();
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), album_uri);
                iv_edit_profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                mProfile.setBackground(new ShapeDrawable(new OvalShape()));
                mProfile.setClipToOutline(true);
                spinner_jobs = (Spinner)findViewById(R.id.spinner_edit_jobs);
                spinner_residence = (Spinner)findViewById(R.id.spinner_edit_residences);
                setJobsToSpinner(user.getJobs());
                setResidenceToSpinner(user.getResidence());
                radioGroup = (RadioGroup)findViewById(R.id.radioGroup_edit_sex);
                radioButton_male = (RadioButton)findViewById(R.id.radioButton_edit_male);
                radioButton_female = (RadioButton)findViewById(R.id.radioButton_edit_female);
                //setSexToRadioButton(user.getSex());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mUserReference.addValueEventListener(userLinstener);
    }

    private void userInfoSave() {
        String nickname = ((EditText)findViewById(R.id.et_edit_nickname)).getText().toString();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String currYear = yearFormat.format(currentTime);
        String birthYear = ((EditText)findViewById(R.id.et_edit_birthYear)).getText().toString();
        String age = Integer.toString(Integer.parseInt(currYear) - Integer.parseInt(birthYear) + 1);

        String job = (String)spinner_jobs.getSelectedItem();
        spinner_jobs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
        RadioButton selectedBtn = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
        String sex = selectedBtn.getText().toString();
        // Write new user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        writeNewUser(user.getUid(), album_uri, nickname, age, sex, job, residence);

        // Go to MainActivity
    }

    private void writeNewUser(String userId, Uri uri, final String nickname, final String age, final String sex, final String job, final String residence) {
        final String profileUri = null;
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String fileName = uid + "_profile.png";
        final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://matchup-7ce60.appspot.com").child("images/profile/" + fileName);
        storageReference.putFile(album_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        User user = new User(uid, profileUri, nickname, age, sex, job, residence);
                        uploadUser(storageReference, user);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void uploadUser(StorageReference storageReference, final User user) {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                user.setProfileUri(uri.toString());
                mUserReference.setValue(user);

                goToMainActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void setJobsToSpinner(String jobs) {
        switch (jobs) {
            case "경영,사무":
                spinner_jobs.setSelection(0);
                break;
            case "영업,고객상담":
                spinner_jobs.setSelection(1);
                break;
            case "IT,인터넷":
                spinner_jobs.setSelection(2);
                break;
            case "디자인":
                spinner_jobs.setSelection(3);
                break;
            case "서비스":
                spinner_jobs.setSelection(4);
                break;
            case "전문직":
                spinner_jobs.setSelection(5);
                break;
            case "의료":
                spinner_jobs.setSelection(6);
                break;
            case "생산,제조":
                spinner_jobs.setSelection(7);
                break;
            case "건설":
                spinner_jobs.setSelection(8);
                break;
            case "유통,무역":
                spinner_jobs.setSelection(9);
                break;
            case "미디어":
                spinner_jobs.setSelection(10);
                break;
            case "교육":
                spinner_jobs.setSelection(11);
                break;
            case "특수계층,공공":
                spinner_jobs.setSelection(12);
                break;
        }
    }

    private void setResidenceToSpinner(String residence) {
        switch (residence) {
            case "강원도":
                spinner_residence.setSelection(0);
                break;
            case "경기도":
                spinner_residence.setSelection(1);
                break;
            case "경상남도":
                spinner_residence.setSelection(2);
                break;
            case "경상북도":
                spinner_residence.setSelection(3);
                break;
            case "광주광역시":
                spinner_residence.setSelection(4);
                break;
            case "대구광역시":
                spinner_residence.setSelection(5);
                break;
            case "대전광역시":
                spinner_residence.setSelection(6);
                break;
            case "부산광역시":
                spinner_residence.setSelection(7);
                break;
            case "서울특별시":
                spinner_residence.setSelection(8);
                break;
            case "세종특별자치시":
                spinner_residence.setSelection(9);
                break;
            case "울산광역시":
                spinner_residence.setSelection(10);
                break;
            case "인천광역시":
                spinner_residence.setSelection(11);
                break;
            case "전라남도":
                spinner_residence.setSelection(12);
                break;
            case "전라북도":
                spinner_residence.setSelection(13);
                break;
            case "제주특별자치도":
                spinner_residence.setSelection(14);
                break;
            case "충청남도":
                spinner_residence.setSelection(15);
                break;
            case "충청북도":
                spinner_residence.setSelection(16);
                break;
        }
    }

    /*
    private void setSexToRadioButton(final String sex) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(sex=="남자") {
                    radioGroup.clearCheck();
                    radioButton_male.setChecked(true);
                } else if(sex=="여자") {
                    radioGroup.clearCheck();
                    radioButton_female.setChecked(true);
                }
            }
        });
    }
     */
}
