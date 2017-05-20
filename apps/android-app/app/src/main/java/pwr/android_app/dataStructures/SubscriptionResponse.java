package pwr.android_app.dataStructures;

public class SubscriptionResponse {

    /* ========================================== DATA ========================================== */

    private Subscription subscritpion = null;

    /* ========================================= GETTERS ======================================== */

    public int getServiceId() {
        return subscritpion.id_service;
    }

    public int getServiceStatus() { return subscritpion.status; }

    public int getServiceRepairerId() { return subscritpion.service_repairer_id; }

    public String getServiceRepairerEmail() {return subscritpion.repairer_email; }

    /* ========================================= CLASSES ======================================== */

    class Subscription {

        // --- DATA --- //
        private int status;
        private int id_service;
        private int service_repairer_id;
        private String repairer_email;
    }

    /* ========================================================================================== */

}
