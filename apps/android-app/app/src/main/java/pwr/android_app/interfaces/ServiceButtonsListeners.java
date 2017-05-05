package pwr.android_app.interfaces;

public interface ServiceButtonsListeners {

    void onStartSubscribingButtonFired(int serviceId);
    void onStopSubscribingButtonFired(int serviceId);
    void onStartRepairServiceButtonFired(int serviceId);
    void onStopRepairServiceButtonFired(int serviceId);
}