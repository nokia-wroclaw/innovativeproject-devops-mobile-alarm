package pwr.android_app.dataStructures;

import java.util.List;

public class SynchronizedData {
    private List<ServiceData> sites;

    public List<ServiceData> getSites() {
        return sites;
    }

    public void setSites(List<ServiceData> sites) {
        this.sites = sites;
    }
}