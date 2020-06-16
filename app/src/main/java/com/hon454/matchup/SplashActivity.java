package com.hon454.matchup;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    private static final int DELAY_MILLIS = 1000;

    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser != null) {
            ValueEventListener currentUserDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() == null) {
                        mCurrentUser.delete();
                        mCurrentUser = null;
                        Log.e(TAG, "Current User doesn't have matching user data");
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "loadAuthUserData:onCancelled", databaseError.toException());
                }
            };
            getUserDatabaseReference().child(mAuth.getUid()).addListenerForSingleValueEvent(currentUserDataListener);
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
