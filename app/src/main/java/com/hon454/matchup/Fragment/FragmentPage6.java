package com.hon454.matchup.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hon454.matchup.R;

public class FragmentPage6 extends Fragment {

    public static FragmentPage6 newInstance() {
        FragmentPage6 FragmentPage6 = new FragmentPage6();
        return FragmentPage6;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page6,container,false);
    }
}
