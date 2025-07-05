package com.prm392g2.prmapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainViewPager extends FragmentStateAdapter {
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<Integer> icons = new ArrayList<>();

    public MainViewPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void addFragment(Fragment fragment, String title, int icon) {
        fragments.add(fragment);
        titles.add(title);
        icons.add(Integer.valueOf(icon));
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
    public String getPageTitle(int position) {
        return titles.get(position);
    }
    public int getIcon(int position) {
        return icons.get(position);
    }
}
