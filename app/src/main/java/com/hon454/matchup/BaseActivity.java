package com.hon454.matchup;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {
    public final static int REQUEST_POST_WRITE = 1;

//    private ProgressDialog mProgressDialog;
//
//    protected void showProgressDialog() {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setMessage("Loading...");
//        }
//
//        mProgressDialog.show();
//    }
//
//    protected void hideProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
//    }

    protected String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    protected DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    protected DatabaseReference getUserDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference().child("users");
    }

    protected void startSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    protected void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
