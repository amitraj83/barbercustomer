package com.amit.barberc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.amit.barberc.adapter.FragmentAdapter;
import com.amit.barberc.fragment.BarberDetailFragment;
import com.amit.barberc.fragment.BarberListFragment;

import com.amit.barberc.util.Global;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Global.initUIActivity(this);

        initUIView();
    }

    private void initUIView() {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new BarberListFragment(), "List of Barbers");
        adapter.addFragment(new BarberDetailFragment(), "Details of Barbers");

        ViewPager viewPager = findViewById(R.id.vwp_main_content);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.tab_main_title);
        viewPagerTab.setViewPager(viewPager);

    }
}
