package com.example.cms;

import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;

import java.io.FileReader;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
public class PagerAdapter extends FragmentStateAdapter{

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new Upload();
        }else{
            return new Manager();
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
