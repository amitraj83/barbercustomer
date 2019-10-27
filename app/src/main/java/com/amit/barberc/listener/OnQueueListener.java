package com.amit.barberc.listener;

import com.amit.barberc.model.BarberUser;

public interface OnQueueListener {
    void OnClickQueue(BarberUser barber);
    void OnClickBarber(BarberUser barber);
}
