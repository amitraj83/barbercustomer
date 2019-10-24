package com.amit.barberc.model;

import java.util.ArrayList;
import java.util.List;

public class BarberUser {
    public String id = "";
    public String name = "";
    public String address = "";
    public String phone = "";
    public String password = "";
    public String pertime = "";
    public List<WorkTime> workTimeList = new ArrayList<>();
    public String avatarurl = "";
    public String logourl = "";

    private class WorkTime {
        public String fromtime = "";
        public String totime = "";
    }
}
