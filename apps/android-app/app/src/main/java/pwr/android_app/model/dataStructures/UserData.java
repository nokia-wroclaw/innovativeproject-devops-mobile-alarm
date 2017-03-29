package pwr.android_app.model.dataStructures;

public class UserData {

    // --- DATA --- //
    private boolean error;
    private int uid;
    private user user;

    // --- GETTERS --- //
    public boolean isError() {return error;}
    public int getUid() {return uid;}

    public String getUserEmail() {return user.email;}
    public String getUserName() {return user.name;}
    public String getUserSurname() {return user.surname;}

    class user
    {
        // --- DATA --- //
        private String email;
        private String name;
        private String surname;
    }
}


