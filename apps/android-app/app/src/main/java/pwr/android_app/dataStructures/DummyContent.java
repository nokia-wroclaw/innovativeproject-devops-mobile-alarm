package pwr.android_app.dataStructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyContent {
    public static final List<ServiceData> ITEMS = new ArrayList<ServiceData>();
    public static final Map<String, ServiceData> ITEM_MAP = new HashMap<String, ServiceData>();

    private static final String[] names = {"Facebook", "Interia", "Onet", "Google", "Youtube"};
    private static final String[] ips = {"192.168.122.133", "192.168.252.101", "192.168.66.56", "192.168.120.42", "192.168.150.125"};
    private static final int COUNT = 5;

    static {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < COUNT; j++) {
                addItem(createDummyItem(j));
            }
        }
    }

    private static void addItem(ServiceData item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.toString(), item);
    }

    private static ServiceData createDummyItem(int position) {
        return new ServiceData(position, ips[position], names[position], 1);
    }
}
