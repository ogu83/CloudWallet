package com.watchmen.cloudwallet;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

@Root
public class WalletVM extends VMBase {
    @ElementList
    public ArrayList<ItemVM> Items;
    @Attribute
    public String FileName;
    @Attribute
    public String Password;

    protected static WalletVM DeSerializeFromXML(String xml) throws Exception {
        Serializer serializer = new Persister();
        WalletVM result = serializer.read(WalletVM.class, xml);
        return result;
    }

    public Collection<ItemVM> FilteredItems(String key) {

        ArrayList<ItemVM> filtered = new ArrayList<>();
        for (ItemVM item : Items)
            if (item.Title.contains(key))
                filtered.add(item);

        Collections.sort(filtered, new Comparator<ItemVM>() {
            @Override
            public int compare(ItemVM o1, ItemVM o2) {
                return o1.Title.compareToIgnoreCase(o2.Title);
            }
        });

        return filtered;
    }

    protected String SerializeToXML(String fileName) throws Exception {
        Serializer serializer = new Persister();
        StringWriter wr = new StringWriter();
        serializer.write(this, wr);
        wr.close();
        String result = wr.toString();
        return result;
    }
}
