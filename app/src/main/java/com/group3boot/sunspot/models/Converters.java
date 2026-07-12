package com.group3boot.sunspot.models;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(value.split(",")));
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) return "";
        return String.join(",", list);
    }
}