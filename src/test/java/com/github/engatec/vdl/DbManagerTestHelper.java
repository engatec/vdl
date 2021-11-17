package com.github.engatec.vdl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.engatec.vdl.db.DbManager;

public class DbManagerTestHelper {

    private static final Path TEST_DB_PATH = Path.of("test.db");
    public static final DbManager DB_MANAGER = new DbManager("jdbc:sqlite:" + TEST_DB_PATH);

    static {
        try {
            Files.deleteIfExists(TEST_DB_PATH);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        DB_MANAGER.init();
    }
}
