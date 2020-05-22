package com.hon454.matchup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hon454.matchup.Adapter.ListViewAdapter;
import com.hon454.matchup.Adapter.ViewPagerAdapter;
import com.hon454.matchup.Database.ListViewItem;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // declare_firebase_Auth
    private DrawerLayout drawerLayout;
    private View drawerView;
    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Drawer Layout 구현
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);
        ImageView btn_open_drawer = (ImageView) findViewById(R.id.btn_open_drawer);
        btn_open_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        //Drawer Layout 내부 이벤트 구현
        ListView listview ;
        ListViewAdapter adapter;

        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listView_drawer);
        listview.setAdapter(adapter);

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_account_circle_black_20dp),"회원 정보");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_20dp),"버전정보");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_help_outline_black_20dp),"도움말");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_logout_black_20dp),"로그아웃");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(getApplicationContext(),UserInfoActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        return;
                    case 2:
                        return;
                    case 3:
                        signOut();
                        break;
                }
            }
        });



        //회원탈퇴 버튼 구현
        mAuth = FirebaseAuth.getInstance();
        TextView tv_deleteID = (TextView)findViewById(R.id.tv_deleteID);
        tv_deleteID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteId();
            }
        });

        //ViewPager, TabLayout 구현
        ViewPager viewPager = findViewById(R.id.viewPager);
        fragmentPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        //Floating Action Button 구현
        FloatingActionButton fab =findViewById(R.id.floating_button);
        fab.setOnClickListener(new FABClickListener());

    }

    //Floating Action Button 클릭 이벤트 처리
    class FABClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            // FAB Click 이벤트 처리 구간
            Intent intent = new Intent(getApplicationContext(), NewPostActivity.class);
            startActivity(intent);
        }
    }
    //Sign out
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void deleteId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }


}
