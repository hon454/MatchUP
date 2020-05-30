package com.hon454.matchup;

import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {
//    private ProgressDialog mProgressDialog;
//
//    public void showProgressDialog() {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setMessage("Loading...");
//        }
//
//        mProgressDialog.show();
//    }
//
//    public void hideProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
//    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
