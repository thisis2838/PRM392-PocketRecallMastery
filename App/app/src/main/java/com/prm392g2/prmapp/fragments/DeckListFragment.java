package com.prm392g2.prmapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.adapters.DeckListViewPagerAdapter;

public class DeckListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deck_list, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        DeckListViewPagerAdapter adapter = new DeckListViewPagerAdapter(requireActivity());
        // Add PublicDeckFragment and SavedDeckFragment
        adapter.addFragment(new PublicDeckFragment(), "Public Deck");
        adapter.addFragment(new SavedDeckFragment(), "Saved Deck");
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();

        return view;
    }
} 