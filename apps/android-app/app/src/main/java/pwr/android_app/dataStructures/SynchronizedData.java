package pwr.android_app.dataStructures;

import java.util.ArrayList;
import java.util.List;

public class SynchronizedData {

    public SynchronizedData() {
        sites = new ArrayList<ServiceData>();
    }
    private List<ServiceData> sites;

    public List<ServiceData> getSites() { return sites; }
    public void setSites(List<ServiceData> sites) {
        this.sites = sites;
    }



    public void addService(ServiceData data) { sites.add(data); }
}