package com.example.androidbarberstaffapp.Interface;

import com.example.androidbarberstaffapp.Model.BarberService;

import java.util.List;

public interface IBarberServicesLoadListener {
    void onBarberServicesLoadSuccess(List<BarberService> barberServiceList);
    void onBarberServicesLoadFailed(String message);
}
