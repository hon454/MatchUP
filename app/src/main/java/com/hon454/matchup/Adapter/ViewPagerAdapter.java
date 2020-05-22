package com.hon454.matchup.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hon454.matchup.Fragment.FragmentPage1;
import com.hon454.matchup.Fragment.FragmentPage2;
import com.hon454.matchup.Fragment.FragmentPage3;
import com.hon454.matchup.Fragment.FragmentPage4;
import com.hon454.matchup.Fragment.FragmentPage5;
import com.hon454.matchup.Fragment.FragmentPage6;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    //Fragment 교체 구현
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentPage1.newInstance();
            case 1:
                return FragmentPage2.newInstance();
            case 2:
                return FragmentPage3.newInstance();
            case 3:
                return FragmentPage4.newInstance();
            case 4:
                return FragmentPage5.newInstance();
            case 5:
                return FragmentPage6.newInstance();
            default:
                return null;
        }

    }


    //Fragment 갯수가 달라지면 바꿔줘야함!
    @Override
    public int getCount() {
        return 6;
    }


    //TabLayout Title 선언
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "시사·이슈";
            case 1:
                return "연애";
            case 2:
                return "스포츠";
            case 3:
                return "푸드";
            case 4:
                return "영화";
            case 5:
                return "기타";
            default:
                return null;
        }
    }
}
