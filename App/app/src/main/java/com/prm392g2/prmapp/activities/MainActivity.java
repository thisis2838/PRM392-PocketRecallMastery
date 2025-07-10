package com.prm392g2.prmapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.adapters.MainViewPager;
import com.prm392g2.prmapp.fragments.ProfileFragment;
import com.prm392g2.prmapp.fragments.HomeFragment;
import com.prm392g2.prmapp.fragments.DeckListFragment;

public class MainActivity extends AppCompatActivity
{

    private Button btnLoginLogout, btnProfile;
    private SharedPreferences prefs;
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_TOKEN = "jwt_token";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.bottomNav);

        MainViewPager adapter = new MainViewPager(this);
        adapter.addFragment(new HomeFragment(), "Home", R.drawable.ic_home);
        adapter.addFragment(new DeckListFragment(), "Decks", R.drawable.ic_decks);
        adapter.addFragment(new ProfileFragment(), "Profile", R.drawable.ic_profile);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->{
            tab.setText(adapter.getPageTitle(position));
            tab.setIcon(adapter.getIcon(position));
        }).attach();
    }
}