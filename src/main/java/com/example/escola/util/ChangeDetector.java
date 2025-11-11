package com.example.escola.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ChangeDetector {
    private ChangeDetector() {

    }
    public static String detectChanges(Object oldObj, Object newObj, Set<String> ignoreFields) {
        if (oldObj == null || newObj == null) return "";
        if (!oldObj.getClass().equals(newObj.getClass())) return "";

        List<String> changes = new ArrayList<>();
        for(Field f : oldObj.getClass().getDeclaredFields()) {
            if(Modifier.isStatic(f.getModifiers())) continue;
            String name = f.getName();
            if (ignoreFields != null  && ignoreFields.contains(name)) continue;
            f.setAccessible(true);
            try {
                Object a = f.get(oldObj);
                Object b = f.get(newObj);
                if(!Objects.equals(a, b)){
                    changes.add(name + ": " + String.valueOf(a)+ " -> " + String.valueOf(b));
                }
            } catch (IllegalAccessException e) {
                //ignora o campo
            }
        }
        return changes.stream().collect(Collectors.joining("; "));
    }
}
