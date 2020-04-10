package com.example.androidbarberstaffapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidbarberstaffapp.Common.Common;
import com.example.androidbarberstaffapp.Interface.IBarberServicesLoadListener;
import com.example.androidbarberstaffapp.Model.BarberService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class DoneServicesActivity extends AppCompatActivity implements IBarberServicesLoadListener {

    @BindView(R.id.txt_customer_name)
    TextView txt_customer_name;

    @BindView(R.id.txt_customer_email)
    TextView txt_customer_email;

    @BindView(R.id.chip_group_services)
    ChipGroup chip_group_services;

    @BindView(R.id.chip_group_shopping)
    ChipGroup chip_group_shopping;

    @BindView(R.id.edit_services)
    AppCompatAutoCompleteTextView edit_services;

    @BindView(R.id.img_customer_hair)
    ImageView img_customer_hair;

    @BindView(R.id.btn_shopping)
    Button btn_shopping;

    @BindView(R.id.btn_finish)
    Button btn_finish;

    AlertDialog dialog;

    IBarberServicesLoadListener iBarberServicesLoadListener;

    HashSet<BarberService> serviceAdded = new HashSet<>();

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_services);

        ButterKnife.bind(this);

        init();

        setCustomerInformation();

        loadBarberServices();

    }

    private void init() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .build();

        inflater = LayoutInflater.from(this);

        iBarberServicesLoadListener = this;
    }

    private void loadBarberServices() {
        dialog.show();

        FirebaseFirestore.getInstance()
                .collection("AllSalons")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selectedSalon.getSalonId())
                .collection("Services")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iBarberServicesLoadListener.onBarberServicesLoadFailed(e.getMessage());

                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    List<BarberService> barberServices = new ArrayList<>();
                    for(DocumentSnapshot barberSnapshot:task.getResult()) {
                        BarberService service = barberSnapshot.toObject(BarberService.class);
                        barberServices.add(service);
                    }
                    iBarberServicesLoadListener.onBarberServicesLoadSuccess(barberServices);

                }
            }
        });

    }

    private void setCustomerInformation() {
        txt_customer_name.setText(Common.currentBookingInformation.getCustomerName());
        txt_customer_email.setText(Common.currentBookingInformation.getCustomerEmail());

    }

    @Override
    public void onBarberServicesLoadSuccess(List<BarberService> barberServiceList) {
        List<String> serviceNames = new ArrayList<>();

        // Sort by name
        Collections.sort(barberServiceList, new Comparator<BarberService>() {
            @Override
            public int compare(BarberService o1, BarberService o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        // Add all names to a list after sort
        for(BarberService barberService:barberServiceList) {
            serviceNames.add(barberService.getName());
        }

        // Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, serviceNames);
        edit_services.setThreshold(1);
        edit_services.setAdapter(adapter);
        edit_services.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Add to chip group
                int index = serviceNames.indexOf(edit_services.getText().toString().trim());

                if(!serviceAdded.contains(barberServiceList.get(index))) {

                    serviceAdded.add(barberServiceList.get(index));
                    Chip item = (Chip) inflater.inflate(R.layout.chip_item, null);
                    item.setText(edit_services.getText().toString());
                    item.setTag(index);
                    edit_services.setText("");

                    item.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chip_group_services.removeView(view);
                            serviceAdded.remove((int)item.getTag());
                        }
                    });

                    chip_group_services.addView(item);
                }
                else {
                    edit_services.setText("");
                }
            }
        });


        dialog.dismiss();

    }

    @Override
    public void onBarberServicesLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();

    }
}
