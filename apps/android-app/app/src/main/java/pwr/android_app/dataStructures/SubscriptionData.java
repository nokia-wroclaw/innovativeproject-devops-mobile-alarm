package pwr.android_app.dataStructures;

public class SubscriptionData {

    /* ========================================== DATA ========================================== */

    private Subscription subscritpion = null;

    /* ========================================= GETTERS ======================================== */

    public int getServiceId() {
        return subscritpion.id_service;
    }

    public int getServiceStatus() { return subscritpion.status; }

    /* ========================================= CLASSES ======================================== */

    class Subscription {

        // --- DATA --- //
        private int status;
        private int id_service;

    }

    /* ========================================================================================== */

}
