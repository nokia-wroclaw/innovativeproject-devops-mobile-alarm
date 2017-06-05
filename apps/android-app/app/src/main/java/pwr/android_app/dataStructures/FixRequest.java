package pwr.android_app.dataStructures;

public class FixRequest {
    /* ========================================== DATA ========================================== */

    private int service_id;
    private boolean in_repairing;

    /* ====================================== CONSTRUCTORS ====================================== */

    public FixRequest(int service_id, boolean in_repairing) {

        this.service_id = service_id;
        this.in_repairing = in_repairing;
    }

    /* ========================================= GETTERS ======================================== */

    public int getService_id() {
        return service_id;
    }

    public boolean isIn_repairing() {
        return in_repairing;
    }

    /* ========================================= SETTERS ======================================== */

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public void setIn_repairing(boolean in_repairing) {
        this.in_repairing = in_repairing;
    }

    /* ========================================================================================== */
}
