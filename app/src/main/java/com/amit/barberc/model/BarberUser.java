package com.amit.barberc.model;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

public class BarberUser {

    public String id = "";
    public String name = "";
    public String address = "";
    public String phoneNumber = "";
    public String password = "";
    public String pertime = "";
    public List<WorkTime> workTimeList = new ArrayList<>();
    public String avatarImgUrl = "";
    public String logoImgUrl = "";
    public String langitude = "";
    public String latitude = "";
    public int customers = 0;

    public void onSetQueueWithCustomer() {
        customers = customers + 1;
    }

    public void onUnsetQueueWithCustomer() {
        customers = customers - 1;
        if (customers < 0) {
            customers = 0;
        }
    }

    @Keep
    static public class WorkTime {
        public String fromtime = "";
        public String totime = "";
    }
}
