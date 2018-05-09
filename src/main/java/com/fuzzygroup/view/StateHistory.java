package com.fuzzygroup.view;

import com.fuzzygroup.view.VisualRepresentationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StateHistory implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(Display.class);

    // todo substitute on list. Adding is order, therefore permanent tree rebalancing procedure.
    private TreeMap<Integer, VisualRepresentationState> states = new TreeMap<>();

    public VisualRepresentationState getFloorStateAt(int timestamp) {
        Map.Entry<Integer, VisualRepresentationState> entry = states.floorEntry(timestamp);
        if (entry == null) {
            try {
                //todo
                logger.error("State for timestamp {} doesn't exist. " +
                                "Probable causes: history is disable " +
                                "or object yet hadn't created at given time. " +
                                "Object first history snapshot at: {}. " +
                                "But probably this snapshot going to remove cause history is disabled.",
                        timestamp, states.firstKey());
                throw new Exception("State for timestamp " + timestamp + " doesn't exist. " +
                        "Probable causes: history is disable " +
                        "or object yet hadn't created at given time. " +
                        "Object first history snapshot at: " + states.firstKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return entry.getValue();
        }
    }

    public VisualRepresentationState getLastState() {
        Map.Entry<Integer, VisualRepresentationState> entry = states.lastEntry();
        if (entry == null) {
            try {
                //todo
                logger.error("States unavailable. Probably synchronization mistake " +
                        "(between cleaning history and save new snapshot).");
                throw new Exception("States unavailable. Probably synchronization mistake " +
                        "(between cleaning history and save new snapshot).");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return entry.getValue();
        }
    }

    public void addStateAt(int timestamp, VisualRepresentationState state) {
        states.put(timestamp, state);
    }

    public void clearAndSave(int timestamp, VisualRepresentationState state) {
        states.clear();
        states.put(timestamp, state);
    }

    class ConsistentAdditionList<K extends Comparable<K>, V>
            extends ArrayList<Entry<K, V>> {

        private final List<Entry<K, V>>
                list = new ArrayList<>();

        public V getByKey(K timestamp) {
            V value = null;
            int heightBound = list.size() - 1;
            int lowBound = 0;
            while (value == null) {
                int middle = lowBound + (heightBound - lowBound) / 2;
                Entry<K, V> entry = list.get(middle);
                K middleKey = entry.getKey();
                if (middleKey.compareTo(timestamp) > 0) {
                    if (middle == 0) {
                        return null;
                    }
                    heightBound = middle;
                } else if (middleKey.compareTo(timestamp) == 0) {
                    value = entry.getValue();
                } else if (
                        list.size() - 1 < middle + 1 ||
                                list.get(middle + 1).getKey().compareTo(timestamp) > 0) {
                    value = entry.getValue();
                } else {
                    lowBound = middle;
                }
            }
            return value;
        }

    }

    static class Entry<K, V> {
        private K key;
        private V value;

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
