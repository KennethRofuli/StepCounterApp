package com.example.stepcounterapp;

import com.google.android.gms.maps.model.LatLng;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for Room TypeConverters
 */
public class ConvertersTest {

    @Test
    public void fromLatLngList_withValidList_returnsValidJson() {
        List<LatLng> latLngList = new ArrayList<>();
        latLngList.add(new LatLng(40.7128, -74.0060));
        latLngList.add(new LatLng(34.0522, -118.2437));

        String json = Converters.fromLatLngList(latLngList);

        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
    }

    @Test
    public void fromLatLngList_withEmptyList_returnsEmptyArray() {
        List<LatLng> emptyList = new ArrayList<>();
        String json = Converters.fromLatLngList(emptyList);

        assertNotNull(json);
        assertEquals("[]", json);
    }

    @Test
    public void toLatLngList_withValidJson_returnsValidList() {
        String json = "[{\"latitude\":40.7128,\"longitude\":-74.0060},{\"latitude\":34.0522,\"longitude\":-118.2437}]";
        List<LatLng> latLngList = Converters.toLatLngList(json);

        assertNotNull(latLngList);
        assertEquals(2, latLngList.size());
        assertEquals(40.7128, latLngList.get(0).latitude, 0.0001);
        assertEquals(-74.0060, latLngList.get(0).longitude, 0.0001);
    }

    @Test
    public void toLatLngList_withEmptyJsonArray_returnsEmptyList() {
        String json = "[]";
        List<LatLng> latLngList = Converters.toLatLngList(json);

        assertNotNull(latLngList);
        assertTrue(latLngList.isEmpty());
    }

    @Test
    public void roundTripConversion_preservesData() {
        List<LatLng> originalList = new ArrayList<>();
        originalList.add(new LatLng(40.7128, -74.0060));
        originalList.add(new LatLng(34.0522, -118.2437));

        String json = Converters.fromLatLngList(originalList);
        List<LatLng> convertedList = Converters.toLatLngList(json);

        assertNotNull(convertedList);
        assertEquals(originalList.size(), convertedList.size());
        assertEquals(originalList.get(0).latitude, convertedList.get(0).latitude, 0.0001);
        assertEquals(originalList.get(0).longitude, convertedList.get(0).longitude, 0.0001);
    }
}
