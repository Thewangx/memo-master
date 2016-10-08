package com.giot.memo;


import android.app.Fragment;

import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;

/**
 * 基本Fragment
 * Created by reed on 16/7/25.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getFragmentName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getFragmentName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected final String getFragmentName() {
        String str = this.toString();
        return str.substring(0, str.indexOf("{"));
    }
}
