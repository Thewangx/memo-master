package com.giot.memo.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.giot.memo.data.entity.Bill;
import com.giot.memo.util.SysConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * bill fragment pagerAdapter
 * Created by reed on 16/8/9.
 */
public class BillFragmentAdapter extends FragmentPagerAdapter{

    private BillFragment preFragment;
    private BillFragment currentFragment;
    private BillFragment nextFragment;
    private List<BillFragment> fragments;


    public BillFragmentAdapter(FragmentManager fm) {
        super(fm);

        preFragment = new BillFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SysConstants.CURRENT, false);
        preFragment.setArguments(bundle);

        Bundle currentBundle = new Bundle();
        currentFragment = new BillFragment();
        currentFragment.setArguments(currentBundle);

        nextFragment = new BillFragment();
        nextFragment.setArguments(bundle);

        fragments = new ArrayList<>();
        fragments.add(preFragment);
        fragments.add(currentFragment);
        fragments.add(nextFragment);

    }

    public void setBills(List<Bill> preBills, List<Bill> currentBills, List<Bill> nextBills) {
        preFragment.setData(preBills);
        currentFragment.setData(currentBills);
        nextFragment.setData(nextBills);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
