package pwr.android_app.dataStructures;

public class ServiceResponse {

    /* ========================================== DATA ========================================== */

    private Service service = null;

    /* ========================================= GETTERS ======================================== */

    public int getId() {
        return service.id;
    }
    public int getOrganizationId() {
        return service.organization_id;
    }
    public String getAddress() {
        return service.address;
    }
    public String getName() {
        return service.name;
    }

    /* ========================================= CLASSES ======================================== */

    class Service {

        // --- DATA --- //
        private int id;
        private int organization_id;
        private String address;
        private String name;
    }

    /* ========================================================================================== */
}