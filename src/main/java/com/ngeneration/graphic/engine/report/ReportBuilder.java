package com.ngeneration.graphic.engine.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportBuilder {
    public static class TreeReport {
        private final Map<Integer, Object> map = new HashMap<>();
        private final Map<Class<?>, String> msgs = new HashMap<>();
        private final StringBuilder stringBuilder = new StringBuilder();
        //        private StringBuilder lastSubTreeString = new StringBuilder();
        private List<String> subTreeMsgs = new ArrayList<>();


        public void append(String string, Object o, int level) {
            Object previousValue = map.put(level, o);
            if (previousValue != o) {
                subTreeMsgs.add(level, string);
                removeRight(subTreeMsgs, level);
            }
        }

        public void append(String string) {
            for (int i = 0; i < subTreeMsgs.size(); i++) {
                if (!subTreeMsgs.get(i).isEmpty()) {
                    stringBuilder.append(tabs(i)).append(subTreeMsgs.get(i)).append("\n");
                }
                subTreeMsgs.remove(i);
                subTreeMsgs.add(i, "");
            }
            stringBuilder.append(tabs(subTreeMsgs.size())).append(string).append("\n");
//            subTreeMsgs.clear();
//            map.clear();
        }

        private String tabs(int number) {
            return repeat("\t", number);
        }

        private String repeat(String string, int number) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < number; i++) {
                result.append(string);
            }
            return result.toString();
        }

        private void removeRight(List<String> list, int index) {
            for (int i = index + 1; i < list.size(); i++) {
                list.remove(i);
            }
        }

        public String build() {
            if (stringBuilder.length() > 0) {
                stringBuilder.insert(0, "==============================================================\n");
            }
            return stringBuilder.toString();
        }
    }
}
