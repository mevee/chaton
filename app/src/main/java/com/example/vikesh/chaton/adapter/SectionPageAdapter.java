package com.example.vikesh.chaton.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.vikesh.chaton.fragments.ChatsFragment;
import com.example.vikesh.chaton.fragments.FriendsFragment;
import com.example.vikesh.chaton.fragments.RequestFragment;

public class SectionPageAdapter extends FragmentPagerAdapter {

    public SectionPageAdapter(FragmentManager fm)
    {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            case 1:
                ChatsFragment chatFragment = new ChatsFragment();
                return chatFragment;
            case 2:
            FriendsFragment friendsFragment = new FriendsFragment();
            return friendsFragment;
                default:
                return null;
        }
    }
    @Override
    public int getCount() {
            return 3;
    }
        public CharSequence getPageTitle(int position)
        {
            switch (position) {
                case 0:
                    return "REQUEST";
                case 1:
                    return "CHATS";
                case 2:
                    return "FRIENDS";
                default:
                    return null;
            }
        }
    }

