package pwr.android_app.dataStructures;

public class Service {

    /* ========================================== DATA ========================================== */

    private int serviceId;
    private int organizationId;

    private String serviceName;
    private String serviceAddress;

    private ServiceStatus serviceStatus;
    private SubscriptionStatus subscriptionStatus;

    /* ====================================== CONSTRUCTORS ====================================== */

    public Service(ServiceData data) {

        this.serviceId = data.getId();
        this.organizationId = data.getOrganizationId();

        this.serviceName = data.getName();
        this.serviceAddress = data.getAddress();

        this.serviceStatus = ServiceStatus.UNKNOWN;
        this.subscriptionStatus = SubscriptionStatus.UNKNOWN;
    }

    /* ========================================= SETTERS ======================================== */

    public void addSubscription (SubscriptionData data) {

        if (data.getServiceId() == this.getServiceId()) {

            this.subscriptionStatus = SubscriptionStatus.SUBSCRIPTION_UP;

            switch (data.getServiceStatus()) {

                case 1:
                    this.serviceStatus = ServiceStatus.UP;
                    break;

                case 2:
                    this.serviceStatus = ServiceStatus.DOWN;
                    break;

                case 3:
                    this.serviceStatus = ServiceStatus.UNSPECIFIED;
                    break;

                default:
                    this.serviceStatus = ServiceStatus.UNKNOWN;
                    break;
            }
        }
    }

    public void removeSubscription() {

        this.serviceStatus = ServiceStatus.UNKNOWN;
        this.subscriptionStatus = SubscriptionStatus.SUBSCRIPTION_DOWN;
    }

    /* ========================================= GETTERS ======================================== */

    public int getServiceId() {
        return serviceId;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    /* ========================================= CLASSES ======================================== */

    public enum ServiceStatus {

        UNKNOWN,
        UP,
        DOWN,
        UNSPECIFIED
    }

    public enum SubscriptionStatus {

        UNKNOWN,
        SUBSCRIPTION_UP,
        SUBSCRIPTION_DOWN
    }

    /* ========================================================================================== */

}
