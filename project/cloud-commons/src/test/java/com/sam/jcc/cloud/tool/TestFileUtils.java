package com.sam.jcc.cloud.tool;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.util.UUID.randomUUID;

/**
 * @author Alexey Zhytnik
 * @since 03-Dec-16
 */
public final class TestFileUtils {

    private static Random random = new Random();
    private static FileManager files = new FileManager();

    public static void createFolder(File dir) {
        files.createDir(dir);
    }

    public static void fileWithRand(File file) {
        files.write(randomContent(), file);
    }

    public static File createInnerFile(File dir) throws IOException {
        final File inner = new File(dir, randomUUID().toString());
        if (!inner.createNewFile()) {
            throw new RuntimeException("Can't create file " + inner);
        }
        return inner;
    }

    public static byte[] randomContent() {
        final byte[] content = new byte[10_000];
        random.nextBytes(content);
        return content;
    }
}
