package com.example.androidbarberstaffapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidbarberstaffapp.Common.Common;
import com.example.androidbarberstaffapp.DoneServicesActivity;
import com.example.androidbarberstaffapp.Interface.IRecyclerItemSelectedListener;
import com.example.androidbarberstaffapp.Model.BookingInformation;
import com.example.androidbarberstaffapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {

    Context context;
    List<BookingInformation> timeSlotList;
    List<CardView> cardViewList;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<BookingInformation> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot, parent, false);
        return new MyViewHolder(itemView );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(position)).toString());

        // all slots are available
        if(timeSlotList.size() == 0) {
            holder.card_time_slot.setCardBackgroundColor( context.getResources().getColor(android.R.color.white));
            holder.txt_time_slot_description.setText("Available");
            holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));

            holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                @Override
                public void onItemSelectedListener(View view, int pos) {

                }
            });

        }

        // some slots are full
        else {

             for(BookingInformation slotValue:timeSlotList) {
                 int slot = Integer.parseInt(slotValue.getSlot().toString());

                 if(slot == position) {

                     holder.card_time_slot.setTag(Common.DISABLE_TAG);
                     holder.card_time_slot.setCardBackgroundColor( context.getResources().getColor(android.R.color.darker_gray));

                     holder.txt_time_slot_description.setText("Full");
                     holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                     holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));

                     holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                         @Override
                         public void onItemSelectedListener(View view, int pos) {

                             // Get Booking info for full time slots
                             FirebaseFirestore.getInstance()
                                     .collection("AllSalons")
                                     .document(Common.state_name)
                                     .collection("Branch")
                                     .document(Common.selectedSalon.getSalonId())
                                     .collection("Barber")
                                     .document(Common.currentBarber.getBarberId())
                                     .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                                     .document(slotValue.getSlot().toString())
                                     .get()
                                     .addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {
                                             Toast.makeText(context, e.getMessage() , Toast.LENGTH_SHORT).show();
                                         }
                                     }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                 @Override
                                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                     if (task.isSuccessful()) {
                                         if (task.getResult().exists()) {
                                             Common.currentBookingInformation = task.getResult().toObject(BookingInformation.class);
                                             context.startActivity(new Intent(context, DoneServicesActivity.class));
                                         }
                                     }

                                 }
                             });

                         }

                     });
                 }
                 else {
                     holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                         @Override
                         public void onItemSelectedListener(View view, int pos) {

                         }
                     });
                 }
             }
        }

        // Add all cards to list
        if(!cardViewList.contains(holder.card_time_slot))
            cardViewList.add(holder.card_time_slot);

    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_time_slot, txt_time_slot_description;
        CardView card_time_slot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
             card_time_slot = (CardView) itemView.findViewById(R.id.card_time_slot);
             txt_time_slot = (TextView) itemView.findViewById(R.id.txt_time_slot);
             txt_time_slot_description = (TextView) itemView.findViewById(R.id.txt_time_slot_description);

             itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
