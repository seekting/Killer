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
import com.seekting.killer.databinding.PersonActivityBinding;
import com.seekting.killer.model.Control;
import com.seekting.killer.model.Person;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PersonControlActivity extends AppCompatActivity implements ConnectManager.PersonListener {

    private PersonActivityBinding mPersonActivityBinding;
    private ConnectManager mConnectManager;
    private List<Person> mPersons = new ArrayList<>();
    private List<String> mSelected = new ArrayList<>();
    private MyAdapter mMyAdapter;
    private LayoutInflater mLayoutInflater;

    public static void start(Context context) {
        context.startActivity(new Intent(context, PersonControlActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        mLayoutInflater = LayoutInflater.from(this);
        mPersonActivityBinding = DataBindingUtil.setContentView(this, R.layout.person_activity);
        mPersonActivityBinding.setActivity(this);
        mConnectManager = ConnectManager.getInstance();

        if (ConnectManager.getInstance().getPersons() != null) {
            mPersons.addAll(ConnectManager.getInstance().getPersons());
        }
        mPersonActivityBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMyAdapter = new MyAdapter();
        mPersonActivityBinding.recyclerView.setAdapter(mMyAdapter);
        mConnectManager.setPersonListener(this);

    }

    private List<String> getSelectedPersons() {
        List<String> people = new ArrayList<>();
        for (Person person : mPersons) {
            if (mSelected.contains(person.getId())) {
                people.add(person.getId());
            }
        }
        return people;

    }

    public void onDieCLick(View v) {
        String action = Control.PERSON_ACTION_DIE;
        sendAction(action);

    }

    public void onAliveCLick(View v) {
        String action = Control.PERSON_ACTION_ALIVE;
        sendAction(action);
    }

    public void onAddCLick(View v) {
        String action = Control.PERSON_ACTION_ADD;
        sendAction(action);
    }

    private void sendAction(String action) {
        List<String> people = getSelectedPersons();
        if (people.isEmpty()) {
            return;
        }
        Control control = new Control();
        control.setAction(action);
        control.setType(Control.TYPE_PERSON);
        control.setIds(people);
        Gson gson = new Gson();
        String str = gson.toJson(control);
        mConnectManager.write(str);
    }


    @Override
    public void onPersonUpdate(List<Person> list) {
        mPersons.clear();
        mPersons.addAll(list);
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
            holder.bind(mPersons.get(position));
            holder.itemView.setTag(mPersons.get(position));

        }

        @Override
        public int getItemCount() {
            return mPersons.size();
        }

        @Override
        public void onClick(View v) {
            Person person = (Person) v.getTag();
            if (mSelected.contains(person.getId())) {
                mSelected.remove(person.getId());

            } else {
                mSelected.add(person.getId());

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

        private void bind(Person person) {
            name.setText(person.getName());
            mCheckedTextView.setChecked(mSelected.contains(person.getId()));
        }
    }
}
