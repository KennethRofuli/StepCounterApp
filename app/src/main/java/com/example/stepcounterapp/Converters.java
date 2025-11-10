package com.example.stepcounterapp;

import androidx.room.TypeConverter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromLatLngList(List<LatLng> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<LatLng> toLatLngList(String json) {
        Type type = new TypeToken<List<LatLng>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
