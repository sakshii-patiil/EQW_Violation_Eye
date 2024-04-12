package com.example.eqwviolationeye;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class pendingAdapter extends FirebaseRecyclerAdapter<pending,pendingAdapter.myviewholder>
{

    public pendingAdapter(@NonNull FirebaseRecyclerOptions<pending> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull pending model) {
        holder.timestamp.setText(model.gettimestamp());
    }


    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_item,parent,false);
        return new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder
    {
        TextView timestamp;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            timestamp=itemView.findViewById(R.id.timestamp);
        }
    }

}

