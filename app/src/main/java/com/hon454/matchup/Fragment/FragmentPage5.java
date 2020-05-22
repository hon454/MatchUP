package com.hon454.matchup.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hon454.matchup.R;

public class FragmentPage5 extends Fragment {

    public static FragmentPage5 newInstance() {
        FragmentPage5 FragmentPage5 = new FragmentPage5();
        return FragmentPage5;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page5,container,false);
    }
}
