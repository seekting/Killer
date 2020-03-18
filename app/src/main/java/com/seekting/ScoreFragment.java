package com.seekting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seekting.common.DialogUtils;
import com.seekting.killer.R;
import com.seekting.killer.ScoreActivity;
import com.seekting.killer.databinding.ScoreFragmentBinding;
import com.seekting.killer.model.ScoreItem;
import com.seekting.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScoreFragment extends Fragment implements View.OnClickListener {

    private View mRootView;
    private ScoreFragmentBinding mScoreFragmentBinding;
    private ScoreItem mScoreItem;
    private List<ScoreItem> mItems = new ArrayList<>();
    private MyAdapter mMyAdapter;
    private LayoutInflater mInflater;
    private ConnectManager mConnectManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mConnectManager = ConnectManager.getInstance();
        mInflater = inflater;
        mScoreFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.score_fragment, container, false);
        mScoreItem = (ScoreItem) getArguments().getSerializable("item");
        init();
        return mScoreFragmentBinding.getRoot();
    }

    private void init() {
        mScoreFragmentBinding.title.setText(mScoreItem.name);
        for (ScoreItem item : mScoreItem.mItems) {
            mItems.add(item);
            if (!item.mItems.isEmpty()) {
                mItems.addAll(item.mItems);
            }

        }
        mMyAdapter = new MyAdapter();

        mScoreFragmentBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mScoreFragmentBinding.recyclerView.setAdapter(mMyAdapter);
        int lineHeight = 1;
        mScoreFragmentBinding.recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            private Paint dividerPaint = new Paint();

            @Override

            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                outRect.top = lineHeight;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);

                int childCount = parent.getChildCount();
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();

                dividerPaint.setColor(Color.DKGRAY);
                for (int i = 0; i < childCount; i++) {
                    View view = parent.getChildAt(i);
                    float top = view.getBottom();
                    float bottom = view.getBottom() + lineHeight;
                    c.drawRect(left, top, right, bottom, dividerPaint);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {

        ScoreItem scoreItem = (ScoreItem) v.getTag();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ScoreActivity scoreActivity = (ScoreActivity) getActivity();
        boolean isRed = scoreActivity.mScoreActivityBinding.radioRed.isChecked();
        String title = String.format(getString(R.string.sure_score), isRed ? getString(R.string.red) : getString(R.string.blue));
        builder.setTitle(title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d("seekting", "ScoreFragment.onClick()" + scoreItem.id);


//                {
//                    "score_group": "0",//0表示红方，1表示蓝方
//                        "score_id": "1-1-1"//1-1-1表示"值班员接到上级命令超过１分钟才报告” //2-1-1 表示：不能有效利用有利地形
//                }
                String str = String.format("{\"score_group\": \"%s\",\"score_id\": \"%s\"}", isRed ? "0" : "1", scoreItem.id);
                mConnectManager.write(str);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        DialogUtils.showDialog(builder.create());

    }

    class Holder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text);

        }
    }

    class MyAdapter extends RecyclerView.Adapter<Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Holder holder = new Holder(mInflater.inflate(R.layout.score_item, parent, false));
            if (viewType == 2) {

                holder.itemView.setBackgroundColor(Color.rgb(79, 131, 253));
                holder.mTextView.setTextColor(Color.WHITE);
            } else if (viewType == 3) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.mTextView.getLayoutParams();
                layoutParams.leftMargin = ScreenUtils.dpToPx(getResources(), 32);
                holder.itemView.setOnClickListener(ScoreFragment.this);
            }
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            return mItems.get(position).level;

        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.mTextView.setText(mItems.get(position).name);
            holder.itemView.setTag(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }
}
