package com.prm392g2.prmapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.adapters.MainViewPager;
import com.prm392g2.prmapp.fragments.DeckListFragment;
import com.prm392g2.prmapp.fragments.HomeFragment;
import com.prm392g2.prmapp.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity
{
    private SharedPreferences prefs;
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_TOKEN = "jwt_token";

    public static ViewPager2 viewPager;
    public static TabLayout tabLayout;
    public static Toolbar toolbar;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main), (v, insets) ->
            {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        );

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.bottomNav);
        fragmentContainer = findViewById(R.id.fragment_container);

        MainViewPager adapter = new MainViewPager(this);
        adapter.addFragment(new HomeFragment(), "Home", R.drawable.ic_home);
        adapter.addFragment(new DeckListFragment(), "Decks", R.drawable.ic_decks);
        adapter.addFragment(new ProfileFragment(), "Profile", R.drawable.ic_decks);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(
            tabLayout, viewPager, (tab, position) ->
        {
            tab.setText(adapter.getPageTitle(position));
            tab.setIcon(adapter.getIcon(position));
        }
        ).attach();

        getSupportFragmentManager().addOnBackStackChangedListener(() ->
        {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment != null)
            {
                hideMainContent();
            }
            else
            {
                showMainContent();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                if (fragmentContainer.getVisibility() == View.VISIBLE &&
                    getSupportFragmentManager().getBackStackEntryCount() > 0)
                {
                    getSupportFragmentManager().popBackStack();
                    if (getSupportFragmentManager().getBackStackEntryCount() <= 1)
                    {
                        showMainContent();
                    }
                }
                else
                {
                    finish();
                }
            }

        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void loadFragment(Fragment fragment)
    {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit();

        hideMainContent();
    }

    public void hideMainContent()
    {
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        toolbar.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void showMainContent()
    {
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);

        toolbar.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void setToolbarTitle(String title)
    {
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
