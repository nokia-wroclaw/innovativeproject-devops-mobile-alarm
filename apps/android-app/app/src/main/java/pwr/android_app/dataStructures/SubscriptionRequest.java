package pwr.android_app.dataStructures;

public class SubscriptionRequest {

    /* ========================================== DATA ========================================== */

    private int id;
    private String status;

    // ......................................... STATIC ......................................... //
    private final static String ADD = "add";
    private final static String REMOVE = "remove";

    /* ====================================== CONSTRUCTORS ====================================== */

    public SubscriptionRequest(int id, SubscriptionStatus status) {

        this.setId(id);
        this.setSubscriptionStatus(status);
    }

    /* ========================================= GETTERS ======================================== */

    public int getId() {
        return this.id;
    }

    public SubscriptionStatus getStatus() {

        switch (status) {

            case ADD:
                return SubscriptionStatus.ADD;

            case REMOVE:
                return SubscriptionStatus.REMOVE;

            default:
                return null;
        }
    }

    /* ========================================= SETTERS ======================================== */

    public void setId(int id) {
        this.id = id;
    }

    public void setSubscriptionStatus(SubscriptionStatus status) {

        switch (status) {

            case ADD:
                this.status = ADD;
                return;

            case REMOVE:
                this.status = REMOVE;
                return;

            default:
                this.status = null;
        }

    }

    /* ========================================= CLASSES ======================================== */

    public enum SubscriptionStatus {

        ADD,
        REMOVE
    }

    /* ========================================================================================== */
}
