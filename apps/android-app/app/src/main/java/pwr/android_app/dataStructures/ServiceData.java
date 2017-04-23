package pwr.android_app.dataStructures;

import android.os.Parcel;
import android.os.Parcelable;

public class ServiceData implements Parcelable {

    // --- DATA --- //
    private int id;
    private String address;
    private String name;
    private int current_state;

    public ServiceData(int id, String address, String name, int current_state) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.current_state = current_state;
    }



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
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    protected ServiceData(Parcel in) {
        id = in.readInt();
        address = in.readString();
        name = in.readString();
        current_state = in.readInt();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(address);
        dest.writeString(name);
        dest.writeInt(current_state);
    }

    public void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.address = in.readString();
        this.name = in.readString();
        this.current_state = in.readInt();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ServiceData> CREATOR = new Parcelable.Creator<ServiceData>() {
        @Override
        public ServiceData createFromParcel(Parcel in) {
            return new ServiceData(in);
        }

        @Override
        public ServiceData[] newArray(int size) {
            return new ServiceData[size];
        }
    };
}