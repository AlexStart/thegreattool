package com.sam.jcc.cloud.utils.files;

import static com.google.common.base.Charsets.UTF_8;
import static com.sam.jcc.cloud.utils.SystemUtils.isWindowsOS;
import static java.nio.file.Files.setAttribute;
import static java.util.Collections.emptyList;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.filefilter.DirectoryFileFilter.DIRECTORY;
import static org.apache.commons.io.filefilter.FileFileFilter.FILE;
import static org.springframework.util.ResourceUtils.JAR_URL_PREFIX;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;
import com.sam.jcc.cloud.PropertyResolver;
import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.i.OSDependent;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Alexey Zhytnik
 * @since 21.11.2016
 */
@Slf4j
@Component
//TODO(a bad part of the app): some methods should be static
public class FileManager {

	private final String fileProtocol = PropertyResolver.getProperty("protocols.file");

	public File getFileByUri(String uri) {
		if (!uri.startsWith(fileProtocol)) {
			log.error("File URI can't start with {}", uri);
			throw new InternalCloudException();
		}
		return new File(uri.substring(fileProtocol.length()));
	}

	public void delete(File dir) {
		if (dir == null) {
			return; // otherwise it kills current dir! TODO
		}

		if (dir.isDirectory()) {
			cleanDir(dir);
		}

		if (!dir.delete()) {
			log.error("Can't delete {}", dir);
			throw new InternalCloudException();
		}
	}

	public void cleanDir(File dir) {
		try {
			FileUtils.cleanDirectory(dir);
		} catch (IOException e) {
			throw new InternalCloudException(e);
		}
	}

	@OSDependent(
	        "Currently supported only for Windows OS, " +
            "Unix hidden files must to start with '.'"
    )
	public void createHiddenDir(File file) {
		createDir(file);
		if (!isWindowsOS()) return;

		try {
			setAttribute(file.toPath(), "dos:hidden", true);
		} catch (IOException e) {
			log.warn("Maybe OS doesn't support this way of creation of hidden directory, {0}", e);
		}
	}

	public void createDir(File file) {
		final boolean removed = !file.exists() || file.delete();

		if (!removed || !file.mkdir()) {
			log.error("Can't create folder {}", file);
			throw new InternalCloudException();
		}
	}

	public void copyDir(File src, File dest) {
		log.info("Copy from {} to {}", src, dest);
		try {
			copyDirectory(src, dest);
		} catch (IOException e) {
			throw new InternalCloudException(e);
		}
	}

	public TempFile createTempDir() {
		return new TempFile(Files.createTempDir());
	}

	public TempFile createTempFile() {
		final String prefix = Integer.toString(new Random().nextInt());
		return createTempFile(prefix, ".tmp");
	}

	public TempFile createTempFile(String prefix, String suffix) {
		try {
			final File temp = File.createTempFile(prefix, suffix);
			return new TempFile(temp);
		} catch (IOException e) {
			throw new InternalCloudException(e);
		}
	}

	public void createFile(File file){
        try {
            Files.createParentDirs(file);
            Files.touch(file);
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }

	public void write(byte[] content, File target) {
		try {
			Files.write(content, target);
		} catch (IOException e) {
			throw new InternalCloudException(e);
		}
	}

    public void append(byte[] content, File target) {
        try {
            java.nio.file.Files.write(target.toPath(), content, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }

    public byte[] read(File file) {
        try {
            return IOUtils.toByteArray(file.toURI());
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }

    public String toString(File file) {
        return new String(read(file), UTF_8);
    }

    public static File getResource(Class<?> clazz, String path) {
        try {
            final ClassPathResource resource = new ClassPathResource(path, clazz);

            if (resource.getURL().toString().startsWith(JAR_URL_PREFIX)) {
                return null;
            }
            return resource.getFile();
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }

    public static byte[] getResourceAsBytes(Class<?> clazz, String path) {
        try (InputStream resource = clazz.getResourceAsStream(path)) {
            final ByteArrayOutputStream data = new ByteArrayOutputStream();
            IOUtils.copy(resource, data);
            return data.toByteArray();
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }

	List<File> getAllDirectoryFiles(File dir) {
		return (List<File>) listFiles(dir, FILE, DIRECTORY);
	}

    public List<File> getDirectoryFiles(File dir) {
        final File[] files = dir.listFiles();

        if (files == null) return emptyList();
        return Arrays.stream(files).collect(Collectors.toList());
    }

	int getNesting(File file) {
		try {
			final String path = file.getCanonicalPath();
			return StringUtils.countMatches(path, '/');
		} catch (IOException e) {
			throw new InternalCloudException(e);
		}
	}
}
