package pwr.android_app.dataStructures;

public class UserData {

    /* ========================================== DATA ========================================== */

    private boolean error;
    private int uid;
    private User user;

    /* ========================================= GETTERS ======================================== */

    public boolean isError() { return error; }
    public int getUid() { return uid; }
    public String getUserEmail() { return user.email; }
    public String getUserName() { return user.name; }
    public String getUserSurname() { return user.surname; }

    /* ========================================= CLASSES ======================================== */

    class User {

        // --- DATA --- //
        private String email;
        private String name;
        private String surname;
    }

    /* ========================================================================================== */
}


