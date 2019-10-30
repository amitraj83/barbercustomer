package com.amit.barberc.listener;

import com.amit.barberc.model.BarberUser;
import com.amit.barberc.model.CustomerUser;

import java.util.List;
import java.util.Map;

public interface OnFireBaseListener {
    void onChangedQueueDataBase(Map<String, List<CustomerUser>> customers);
    void onChangedBarberDataBase(List<BarberUser> barbers);
    void onChangedCustomDataBase(CustomerUser user);
}
