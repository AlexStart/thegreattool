package com.sam.jcc.cloud.utils.files;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
public class GzipTarArchiveManager {

    public File unGzip(File tarFile) throws IOException {
        InputStream inputStream = new FileInputStream(tarFile);
        BufferedInputStream in = new BufferedInputStream(inputStream);
        File tar = new FileManager().createTempFile();
        OutputStream out = new FileOutputStream(tar);
        GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
        final byte[] buffer = new byte[1000];
        int n = 0;
        while (-1 != (n = gzIn.read(buffer))) {
            out.write(buffer, 0, n);
        }
        out.close();
        gzIn.close();
        return tar;
    }

    public File unTar(File tarFile, File destination) throws IOException {
        InputStream inputStream = new FileInputStream(tarFile);
        TarArchiveInputStream in = new TarArchiveInputStream(inputStream);
        TarArchiveEntry entry = in.getNextTarEntry();
        File root = entry != null && entry.isDirectory() ? new File(destination, entry.getName()) : destination;
        while (entry != null) {
            if (entry.isDirectory()) {
                entry = in.getNextTarEntry();
                continue;
            }
            File curfile = new File(destination, entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            OutputStream out = new FileOutputStream(curfile);
            IOUtils.copy(in, out);
            out.close();
            entry = in.getNextTarEntry();
        }
        in.close();
        return root;
    }
}
