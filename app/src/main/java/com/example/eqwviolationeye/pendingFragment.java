package com.example.eqwviolationeye;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private DatabaseReference GroupRef,statusRef;
    private String timestamp;

    public pendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        group_fregment_view = inflater.inflate(R.layout.fragment_pending, container, false);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        String id = currentUser.getEmail();
        id = id.substring(0,id.length()-10);
        GroupRef = FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/").getReference(id).child("pending");

        RetriveAndDisplayGroup();
//        InitializeFields();

        listView = (ListView) group_fregment_view.findViewById(R.id.pendingList);



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
                        if (status != null && status) {
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
//                    timestamp = dataSnapshot.getKey();
//                    statusRef = GroupRef.child(timestamp).child("status");
//
////                    Toast.makeText(getActivity(), statusRef.toString(), Toast.LENGTH_SHORT).show();
//                    Log.i("Verify Link : ", statusRef.toString());


//                    statusRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
////                ArrayList list = new ArrayList();
//                            arrayList.clear();
//                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                Toast.makeText(getActivity(),dataSnapshot.getKey().toString(), Toast.LENGTH_SHORT).show();
//                                if((dataSnapshot.getValue()).toString().equals("true"))
//                                {
//                                    arrayList.add(new pending(timestamp));
//                                    Log.d("Elements in List : ", arrayList.toString());
//                                    System.err.println(dataSnapshot.getKey());
//                                }
////                    Log.d("Elements in List : ", arrayList.toString());
////                    System.err.println(dataSnapshot.getKey());
//                            }
//                            pendingAdapter arrayAdapter = new pendingAdapter(getContext(), arrayList);
//                            listView.setAdapter(arrayAdapter);
//                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                    pending p = arrayList.get(position);
////
//                                    String selectedItem = p.getDate() + "," + p.getTime();
//                                    Toast.makeText(getActivity(), selectedItem, Toast.LENGTH_SHORT).show();
//                                    Intent i = new Intent(getContext(), postScreen.class);
//                                    i.putExtra("date", selectedItem);
//                                    startActivity(i);
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });

                                           //statusRef.setValue("false");
//                    arrayList.add(new pending(dataSnapshot.getKey()));
//                    Log.d("Elements in List : ", arrayList.toString());
//                    System.err.println(dataSnapshot.getKey());
//                                       }
//                Log.i("Arraylist Content : ", String.valueOf(arrayList));
//                pendingAdapter arrayAdapter = new pendingAdapter(getContext(), arrayList);
//                listView.setAdapter(arrayAdapter);
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        pending p = arrayList.get(position);
////
//                        String selectedItem = p.getDate()+","+p.getTime();
//                        Toast.makeText(getActivity(),selectedItem,Toast.LENGTH_SHORT).show();
//                        Intent i = new Intent(getContext(),postScreen.class);
//                        i.putExtra("date",selectedItem);
//                        startActivity(i);
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
        });



    }


//    private void fetchStatus() {
//
//        statusRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                ArrayList list = new ArrayList();
//                arrayList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Toast.makeText(getActivity(),dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
//                    if((dataSnapshot.getValue()).toString().equals("true"))
//                    {
//                        arrayList.add(new pending(timestamp));
//                        Log.d("Elements in List : ", arrayList.toString());
//                    System.err.println(dataSnapshot.getKey());
//                    }
////                    Log.d("Elements in List : ", arrayList.toString());
////                    System.err.println(dataSnapshot.getKey());
//                }
//                pendingAdapter arrayAdapter = new pendingAdapter(getContext(), arrayList);
//                listView.setAdapter(arrayAdapter);
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        pending p = arrayList.get(position);
////
//                        String selectedItem = p.getDate() + "," + p.getTime();
//                        Toast.makeText(getActivity(), selectedItem, Toast.LENGTH_SHORT).show();
//                        Intent i = new Intent(getContext(), postScreen.class);
//                        i.putExtra("date", selectedItem);
//                        startActivity(i);
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

            private void InitializeFields() {

    }
}