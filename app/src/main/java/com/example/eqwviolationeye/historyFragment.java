package com.example.eqwviolationeye;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link historyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class historyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View group_fregment_view;
    private ListView listView;
    static public String id;

    private DatabaseReference GroupRef,statusRef;
    private String timestamp;


    public historyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment historyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static historyFragment newInstance(String param1, String param2) {
        historyFragment fragment = new historyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        group_fregment_view = inflater.inflate(R.layout.fragment_history, container, false);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        String id = currentUser.getEmail();
        id = id.substring(0,id.length()-10);
        GroupRef = FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/").getReference(id).child("pending");

        RetriveAndDisplayGroup();
//        InitializeFields();

        listView = (ListView) group_fregment_view.findViewById(R.id.historyList);



        return group_fregment_view;
    }

    private void RetriveAndDisplayGroup() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<pending> arrayList = new ArrayList<>(); // Initialize ArrayList to store pending items
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.i("Datasnapshot Content : ", String.valueOf(dataSnapshot));
                    // Get the value of 'status' from the snapshot'

                    if (dataSnapshot.hasChild("status")) {
                        // Get the value of 'status' from the snapshot
                        String statusStr = dataSnapshot.child("status").getValue(String.class);

                        // Parse the status string to a Boolean
                        Boolean status = Boolean.parseBoolean(statusStr);
                        Log.i("status Content : ", String.valueOf(status));
                        // Check if status is true and add the timestamp (key) to ArrayList
                        if (status != null && !status) {
                            arrayList.add((new pending(dataSnapshot.getKey())));
                            Log.d("Elements in List : ", arrayList.toString());
                            System.err.println(timestamp);
                        }
                    }
                }
                Log.i("Arraylist Content : ", String.valueOf(arrayList));
                // Create adapter and set it to the ListView
                pendingAdapter arrayAdapter = new pendingAdapter(getContext(), arrayList);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pending p = arrayList.get(position);
                        String selectedItem = p.getDate() + "," + p.getTime();
                        Toast.makeText(getActivity(), selectedItem, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getContext(), postScreen.class);
                        i.putExtra("date", selectedItem);
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("Firebase Error", error.getMessage());
            }

        });
    }
}