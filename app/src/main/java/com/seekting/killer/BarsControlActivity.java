package com.seekting.killer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.seekting.ConnectManager;
import com.seekting.killer.databinding.BarsActivityBinding;
import com.seekting.killer.model.Bar;
import com.seekting.killer.model.Control;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BarsControlActivity extends AppCompatActivity implements ConnectManager.BarListener {

    private BarsActivityBinding mBarsActivityBinding;
    private ConnectManager mConnectManager;
    private List<Bar> mBars = new ArrayList<>();
    private List<String> mSelected = new ArrayList<>();
    private MyAdapter mMyAdapter;
    private LayoutInflater mLayoutInflater;

    public static void start(Context context) {
        context.startActivity(new Intent(context, BarsControlActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        mLayoutInflater = LayoutInflater.from(this);
        mBarsActivityBinding = DataBindingUtil.setContentView(this, R.layout.bars_activity);
        mBarsActivityBinding.setActivity(this);
        mConnectManager = ConnectManager.getInstance();

        if (ConnectManager.getInstance().getBars() != null) {
            mBars.addAll(ConnectManager.getInstance().getBars());
        }
        mBarsActivityBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMyAdapter = new MyAdapter();
        mBarsActivityBinding.recyclerView.setAdapter(mMyAdapter);
        mConnectManager.setBarListener(this);

    }

    private List<String> getSelectedBars() {
        List<String> bars = new ArrayList<>();
        for (Bar bar : mBars) {
            if (mSelected.contains(bar.getId())) {
                bars.add(bar.getId());
            }
        }
        return bars;

    }

    public void onUpClick(View v) {
        String action = Control.BAR_ACTION_UP;
        sendAction(action);

    }

    public void onDownClick(View v) {
        String action = Control.BAR_ACTION_DOWN;
        sendAction(action);
    }

    public void onScreamClick(View v) {
        String action = Control.BAR_ACTION_SCREAM;
        sendAction(action);
    }

    public void onSoundClick(View v) {
        String action = Control.BAR_ACTION_SOUND;
        sendAction(action);
    }

    private void sendAction(String action) {
        List<String> people = getSelectedBars();
        if (people.isEmpty()) {
            return;
        }
        Control control = new Control();
        control.setAction(action);
        control.setType(Control.TYPE_BAR);
        control.setIds(people);
        Gson gson = new Gson();
        String str = gson.toJson(control);
        mConnectManager.write(str);
    }


    @Override
    public void onBarUpdate(List<Bar> list) {
        mBars.clear();
        mBars.addAll(list);
        mMyAdapter.notifyDataSetChanged();
    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = mLayoutInflater.inflate(R.layout.item, parent, false);
            v.setOnClickListener(this);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(mBars.get(position));
            holder.itemView.setTag(mBars.get(position));

        }

        @Override
        public int getItemCount() {
            return mBars.size();
        }

        @Override
        public void onClick(View v) {
            Bar bar = (Bar) v.getTag();
            if (mSelected.contains(bar.getId())) {
                mSelected.remove(bar.getId());

            } else {
                mSelected.add(bar.getId());

            }
            notifyDataSetChanged();


        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private CheckedTextView mCheckedTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item);
            mCheckedTextView = itemView.findViewById(R.id.checkbox);
        }

        private void bind(Bar bar) {
            name.setText(bar.getName());
            mCheckedTextView.setChecked(mSelected.contains(bar.getId()));
        }
    }
}
