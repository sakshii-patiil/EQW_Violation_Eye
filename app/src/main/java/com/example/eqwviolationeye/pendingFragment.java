package com.example.eqwviolationeye;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class pendingFragment extends Fragment {
    private View group_fregment_view;
    private ListView listView;
    static public String id;
    private ArrayList<pending> arrayList;

    private DatabaseReference GroupRef;

    public pendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        group_fregment_view = inflater.inflate(R.layout.fragment_pending, container, false);
        id = getActivity().getIntent().getStringExtra("id");
        GroupRef = FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/").getReference(id).child("pending");


        RetriveAndDisplayGroup();
//        InitializeFields();

        listView = (ListView) group_fregment_view.findViewById(R.id.pendingList);
        arrayList = new ArrayList<>();


        return group_fregment_view;
    }

    private void RetriveAndDisplayGroup() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList list = new ArrayList();
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList.add(new pending(dataSnapshot.getKey()));
                    Log.d("Elements in List : ", arrayList.toString());
                    System.err.println(dataSnapshot.getKey());
                }
                pendingAdapter arrayAdapter = new pendingAdapter(getContext(), arrayList);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pending p = arrayList.get(position);
//                        Toast.makeText(getActivity(),p.getTimestamp(),Toast.LENGTH_SHORT).show();
                        String selectedItem = p.getTimestamp();
                        Intent i = new Intent(getContext(),postScreen.class);
                        i.putExtra("date",selectedItem);
                        startActivity(i);
                    }
                });

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void InitializeFields() {

    }
}