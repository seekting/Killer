package com.seekting.killer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.seekting.BaseActivity;
import com.seekting.ScoreFragment;
import com.seekting.killer.databinding.ScoreActivityBinding;
import com.seekting.killer.model.ScoreItem;
import com.seekting.killer.model.ScoreItemManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ScoreActivity extends BaseActivity {

    public ScoreActivityBinding mScoreActivityBinding;
    private List<ScoreFragment> mScoreFragments;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ScoreActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScoreActivityBinding = DataBindingUtil.setContentView(ScoreActivity.this, R.layout.score_activity);
        mScoreFragments = new ArrayList<>();
        ScoreItem scoreItem = ScoreItemManager.getInstance().getScoreItem();

        for (ScoreItem item : scoreItem.mItems) {
            ScoreFragment scoreFragment = new ScoreFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", item);
            scoreFragment.setArguments(bundle);
            mScoreFragments.add(scoreFragment);
        }

        mScoreActivityBinding.vp.setAdapter(new ScoreFragmentPagerAdapter(this, mScoreFragments));

    }

    class ScoreFragmentPagerAdapter extends FragmentStateAdapter {

        List<ScoreFragment> list;

        public ScoreFragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<ScoreFragment> list) {
            super(fragmentActivity);
            this.list = list;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return list.get(position);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}