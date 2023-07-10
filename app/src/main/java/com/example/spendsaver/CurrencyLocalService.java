package com.example.spendsaver;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

public class CurrencyLocalService extends Service {
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        CurrencyLocalService getService() {
            return CurrencyLocalService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // Local Service retrieves currency exchange
    public double getCurrencyChange() {
        SharedPreferences sp = getSharedPreferences("CurrencyChange", MODE_PRIVATE);
        String currency = sp.getString("Currency", "");

        if (currency.equals("€")) {
            return 0.88;
        } else if (currency.equals("$")) {
            return 1.25;
        } else {
            return 1;
        }

    }
}
