package pwr.android_app.dataStructures;

public class ServiceData {

    public ServiceData(int id, String address, String name, int current_state) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.current_state = current_state;
    }

    // --- DATA --- //
    private int id;
    private String address;
    private String name;
    private int current_state;


    // --- GETTERS --- //
    public int getId() {
        return this.id;
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return this.name;
    }

    public int getCurrent_state() {
        return this.current_state;
    }

    // --- SETTERS --- //

    public void setCurrent_state(int state) { this.current_state = state; }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
