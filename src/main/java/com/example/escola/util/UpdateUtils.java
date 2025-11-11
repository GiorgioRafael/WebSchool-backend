package com.example.escola.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Utility to apply non-null properties from a DTO onto an entity and
 * produce a human readable diff string describing changed fields.
 *
 * Usage:
 * String diff = UpdateUtils.applyNonNullPropertiesAndDetect(entity, dto, Set.of("id","createdAt","senha"));
 * if (!diff.isBlank()) { repo.save(entity); log.setDetails(diff); }
 */
public final class UpdateUtils {
    private UpdateUtils() { }

    public static String applyNonNullPropertiesAndDetect(Object entity, Object dto, Set<String> ignoreFields) {
        if (entity == null || dto == null) return "";

        List<String> changes = new ArrayList<>();
        Class<?> dtoClass = dto.getClass();
        Class<?> entityClass = entity.getClass();

        for (Field dtoField : dtoClass.getDeclaredFields()) {
            if (Modifier.isStatic(dtoField.getModifiers())) continue;
            String name = dtoField.getName();
            if (ignoreFields != null && ignoreFields.contains(name)) continue;

            dtoField.setAccessible(true);
            try {
                Object newValue = dtoField.get(dto);
                if (newValue == null) continue; // only apply non-null values

                Field entField;
                try {
                    entField = findFieldInClassHierarchy(entityClass, name);
                } catch (NoSuchFieldException nsf) {
                    // field not present on entity -> ignore
                    continue;
                }

                if (Modifier.isStatic(entField.getModifiers())) continue;
                entField.setAccessible(true);

                Object oldValue = entField.get(entity);
                Object converted = tryConvert(newValue, entField.getType());

                if (Objects.equals(oldValue, converted)) continue;

                // apply
                entField.set(entity, converted);
                changes.add(name + ": " + String.valueOf(oldValue) + " -> " + String.valueOf(converted));

            } catch (IllegalAccessException ignored) {
                // ignore inaccessible fields
            }
        }

        return String.join("; ", changes);
    }

    private static Field findFieldInClassHierarchy(Class<?> clazz, String name) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static Object tryConvert(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isAssignableFrom(value.getClass())) return value;

        // numbers
        if (value instanceof Number) {
            Number n = (Number) value;
            if (targetType == Long.class || targetType == long.class) return n.longValue();
            if (targetType == Integer.class || targetType == int.class) return n.intValue();
            if (targetType == Short.class || targetType == short.class) return n.shortValue();
            if (targetType == Byte.class || targetType == byte.class) return n.byteValue();
            if (targetType == Double.class || targetType == double.class) return n.doubleValue();
            if (targetType == Float.class || targetType == float.class) return n.floatValue();
        }

        // booleans
        if ((targetType == Boolean.class || targetType == boolean.class) && value instanceof String) {
            String s = ((String) value).trim().toLowerCase();
            if (s.equals("true") || s.equals("1") || s.equals("yes")) return true;
            if (s.equals("false") || s.equals("0") || s.equals("no")) return false;
        }

        // String -> enum
        if (targetType.isEnum() && value instanceof String) {
            try {
                @SuppressWarnings({"rawtypes", "unchecked"})
                Object e = Enum.valueOf((Class<? extends Enum>) targetType, ((String) value).trim());
                return e;
            } catch (Exception ignored) {
            }
        }

        // try convert String to number
        if (value instanceof String) {
            String s = ((String) value).trim();
            try {
                if (targetType == Long.class || targetType == long.class) return Long.parseLong(s);
                if (targetType == Integer.class || targetType == int.class) return Integer.parseInt(s);
                if (targetType == Short.class || targetType == short.class) return Short.parseShort(s);
                if (targetType == Byte.class || targetType == byte.class) return Byte.parseByte(s);
                if (targetType == Double.class || targetType == double.class) return Double.parseDouble(s);
                if (targetType == Float.class || targetType == float.class) return Float.parseFloat(s);
                if (targetType == Boolean.class || targetType == boolean.class) {
                    String low = s.toLowerCase();
                    if (low.equals("true") || low.equals("1") || low.equals("yes")) return true;
                    if (low.equals("false") || low.equals("0") || low.equals("no")) return false;
                }
            } catch (Exception ignored) {
            }
        }

        // fallback: return original value (might cause IllegalArgumentException when setting field)
        return value;
    }
}

