package com.github.engatec.vdl.preference;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PortablePreferences extends Preferences {

    private static final Logger LOGGER = LogManager.getLogger(PortablePreferences.class);
    private final Path PREF_PATH = Path.of(StringUtils.defaultString(System.getProperty("app.dir"), StringUtils.EMPTY)).resolve("pref.properties");

    private final Properties properties = new Properties();

    public PortablePreferences() {
        if (Files.exists(PREF_PATH)) {
            try (var is = new FileInputStream(PREF_PATH.toFile())) {
                properties.load(is);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void put(String key, String value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }

        properties.put(key, value);
    }

    @Override
    public String get(String key, String def) {
        Objects.requireNonNull(key);
        return (String) properties.getOrDefault(key, def);
    }

    @Override
    public void remove(String key) {
        properties.remove(key);
    }

    @Override
    public void clear() throws BackingStoreException {
        properties.clear();
    }

    @Override
    public void putInt(String key, int value) {
        put(key, Integer.toString(value));
    }

    @Override
    public int getInt(String key, int def) {
        int result = def;
        String value = get(key, null);
        if (value != null) {
            try {
                result = Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                // Ignoring exception causes specified default to be returned
            }
        }

        return result;
    }

    @Override
    public void putLong(String key, long value) {
        put(key, Long.toString(value));
    }

    @Override
    public long getLong(String key, long def) {
        long result = def;
        String value = get(key, null);
        if (value != null) {
            try {
                result = Long.parseLong(value);
            } catch (NumberFormatException ignored) {
                // Ignoring exception causes specified default to be returned
            }
        }

        return result;
    }

    @Override
    public void putBoolean(String key, boolean value) {
        put(key, String.valueOf(value));
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        boolean result = def;
        String value = get(key, null);
        if (value != null) {
            if (value.equalsIgnoreCase("true")) {
                result = true;
            } else if (value.equalsIgnoreCase("false")) {
                result = false;
            }
        }

        return result;
    }

    @Override
    public void putFloat(String key, float value) {
        put(key, Float.toString(value));
    }

    @Override
    public float getFloat(String key, float def) {
        float result = def;
        String value = get(key, null);
        if (value != null) {
            try {
                result = Float.parseFloat(value);
            } catch (NumberFormatException ignored) {
                // Ignoring exception causes specified default to be returned
            }
        }

        return result;
    }

    @Override
    public void putDouble(String key, double value) {
        put(key, Double.toString(value));
    }

    @Override
    public double getDouble(String key, double def) {
        double result = def;
        String value = get(key, null);
        if (value != null) {
            try {
                result = Double.parseDouble(value);
            } catch (NumberFormatException ignored) {
                // Ignoring exception causes specified default to be returned
            }
        }

        return result;
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        throw new NotImplementedException();
    }

    @Override
    public byte[] getByteArray(String key, byte[] def) {
        throw new NotImplementedException();
    }

    @Override
    public String[] keys() {
        throw new NotImplementedException();
    }

    @Override
    public String[] childrenNames() {
        throw new NotImplementedException();
    }

    @Override
    public Preferences parent() {
        throw new NotImplementedException();
    }

    @Override
    public Preferences node(String pathName) {
        throw new NotImplementedException();
    }

    @Override
    public boolean nodeExists(String pathName) {
        throw new NotImplementedException();
    }

    @Override
    public void removeNode() {
        throw new NotImplementedException();
    }

    @Override
    public String name() {
        throw new NotImplementedException();
    }

    @Override
    public String absolutePath() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isUserNode() {
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    @Override
    public void flush() {
        try (var os = new FileOutputStream(PREF_PATH.toFile())) {
            properties.store(os, null);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void sync() {
        throw new NotImplementedException();
    }

    @Override
    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        throw new NotImplementedException();
    }

    @Override
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        throw new NotImplementedException();
    }

    @Override
    public void addNodeChangeListener(NodeChangeListener ncl) {
        throw new NotImplementedException();
    }

    @Override
    public void removeNodeChangeListener(NodeChangeListener ncl) {
        throw new NotImplementedException();
    }

    @Override
    public void exportNode(OutputStream os) {
        throw new NotImplementedException();
    }

    @Override
    public void exportSubtree(OutputStream os) {
        throw new NotImplementedException();
    }
}
