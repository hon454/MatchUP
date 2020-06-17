package com.hon454.matchup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    private EditText mSearch;
    private ImageView btn_back;
    private ImageView btn_search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearch = (EditText)findViewById(R.id.et_search);
        mSearch.requestFocus(); // editText 활성화

        // 뒤로가기 버튼
        btn_back = (ImageView)findViewById(R.id.btn_back3);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                finish();
            }
        });

        // 돋보기 버튼
        btn_search = (ImageView)findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra(SearchResultActivity.EXTRA_SEARCH_KEY,mSearch.getText().toString());
                startActivity(intent);
                hideKeyboard();
                finish();
            }
        });


        //키보드 띄우기
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        // 키보드 돋보기 버튼을 눌렀을 때
        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                    intent.putExtra(SearchResultActivity.EXTRA_SEARCH_KEY,mSearch.getText().toString());
                    startActivity(intent);
                    hideKeyboard();
                    finish();

                    return true;
                }
                return false;
            }
        });
    }

    //키보드 숨기기
    private void hideKeyboard() {
        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
