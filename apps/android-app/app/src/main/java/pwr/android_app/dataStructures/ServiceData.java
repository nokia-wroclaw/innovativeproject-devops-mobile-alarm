package pwr.android_app.dataStructures;

import android.os.Parcel;
import android.os.Parcelable;

public class ServiceData implements Parcelable {

    /* ========================================== DATA ========================================== */

    private Service service = null;

    /* ====================================== CONSTRUCTORS ====================================== */

    public ServiceData(int id, String address, String name, int current_state) {
        service = new Service();
        service.id = id;
        service.address = address;
        service.name = name;
        service.current_state = current_state;
    }
    private ServiceData(Parcel in) {
        service.id = in.readInt();
        service.address = in.readString();
        service.name = in.readString();
        service.current_state = in.readInt();
    }

    /* ========================================= GETTERS ======================================== */

    public int getId() {
        return service.id;
    }
    public String getAddress() {
        return service.address;
    }
    public String getName() {
        return service.name;
    }
    public int getCurrent_state() {
        return service.current_state;
    }

    /* ========================================= SETTERS ======================================== */

    public void setCurrent_state(int state) { service.current_state = state; }
    public void setAddress(String address) { service.address = address; }
    public void setName(String name) { service.name = name; }
    public void setId(int id) { service.id = id; }

    /* ========================================= METHODS ======================================== */

    // --------------------------------------- Parcelable --------------------------------------- //
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(service.id);
        dest.writeString(service.address);
        dest.writeString(service.name);
        dest.writeInt(service.current_state);
    }

    public void readFromParcel(Parcel in) {
        service.id = in.readInt();
        service.address = in.readString();
        service.name = in.readString();
        service.current_state = in.readInt();
    }

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

    /* ========================================= CLASSES ======================================== */

    class Service {

        // --- DATA --- //
        private int id;
        private String address;
        private String name;
        private int current_state;
    }

    /* ========================================================================================== */
}