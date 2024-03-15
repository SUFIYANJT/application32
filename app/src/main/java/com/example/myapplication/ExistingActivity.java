package com.example.myapplication;

import static com.example.myapplication.LoginActivity.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Support.Activity;
import com.example.myapplication.model.ItemModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ExistingActivity extends Fragment implements SearchableFragment {

    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private List<ItemModel> itemList;
    private ItemModel itemModel;
    ArrayList<Activity> activities=new ArrayList<>();

    public ExistingActivity() {
        // Required empty public constructor
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_existing_activity, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        itemModel = new ViewModelProvider(requireActivity()).get(ItemModel.class);
        itemModel.getActivityMutableLiveData().observe(getViewLifecycleOwner(), newData -> {
            if (newData != null) {
                activities.clear();
                activities.addAll(newData);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onCreateView: existing activity data" + newData.size());
            }else{
                Log.d(TAG, "onCreateView: new data is null ");
            }
        });
        itemModel.getIntegerMutableLiveData().observe(getViewLifecycleOwner(), newData -> {
            Log.d(TAG, "onCreateView: getting the data "+newData);
            activities.remove(activities.get(newData));
            adapter.notifyItemRemoved(newData);
            Toast.makeText(this.requireActivity(), "activity changed ", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreateView: activity size "+activities.size());
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        Log.d(TAG, "onCreateView: arraylist is "+activities.size());
        adapter = new CustomAdapter(activities, false, false,false);
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        adapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Activity item, int position) {
                MyBottomSheetFragmentExixting bottomSheetFragment = new MyBottomSheetFragmentExixting(item,activities,position);
                bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
            }
            @Override
            public void onItemClick(Activity item, int position, String message) {}
        });
        return view;
    }
    @Override
    public void updateSearchQuery(String query) {
        List<Activity> filteredList = new ArrayList<>();
        for (Activity item:activities) {
            if (item.activityName.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.setFilteredList(filteredList);
    }

}

