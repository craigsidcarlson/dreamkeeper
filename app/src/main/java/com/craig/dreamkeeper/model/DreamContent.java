package com.craig.dreamkeeper.model;

import android.view.View;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DreamContent {

    public static List<Dream> ITEMS = new ArrayList<Dream>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<UUID, Dream> ITEM_MAP = new HashMap<UUID, Dream>();

    public static void addItem(Dream item) {
        ITEM_MAP.put(item.id, item);
        ITEMS.add(item);
    }

    public static class Dream {
        public final UUID id;
        private Calendar date;
        private String dreamContent;
        public boolean saved;

        public Dream() {
            date = GregorianCalendar.getInstance();
            id = UUID.randomUUID();
        }

        public Dream(String content) {
            this();
            dreamContent = content;
        }

        public Dream(Calendar d, String content) {
            this();
            date = d;
            dreamContent = content;
        }

        public String getDreamContent() {
            return dreamContent;
        }

        public void setDreamContent(String dC) {
            dreamContent = dC;
        }

        public String getDate() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MMM_dd_KK_mm");
            return formatter.format(date.getTime());
        }
    }

}
