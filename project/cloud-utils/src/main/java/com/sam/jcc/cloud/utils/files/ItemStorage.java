package com.sam.jcc.cloud.utils.files;

import com.sam.jcc.cloud.exception.InternalCloudException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.function.Function;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
@Slf4j
public class ItemStorage<T> {

    @Getter
    private File root;

    private final Function<T, String> itemMapper;

    private final FileManager files = new FileManager();

    public ItemStorage(Function<T, String> itemMapper) {
        this.itemMapper = itemMapper;
    }

    public File create(T item) {
        log.info("Creation storage of {}", item);
        failOnExist(item);

        final File dir = getNotSecured(item);
        log.debug("{} will be placed in {}", item, dir);

        files.createHiddenDir(dir);
        return dir;
    }

    private void failOnExist(T item) {
        if (isExist(item)) {
            throw new ItemAlreadyExistsException();
        }
    }

    public boolean isExist(T item) {
        log.debug("Checking existence of {}", item);
        return getNotSecured(item).exists();
    }

    public void delete(T item) {
        log.info("Delete {}", item);
        files.delete(get(item));
    }

    public File get(T item) {
        final File dir = getNotSecured(item);
        failOnNotExist(item);
        return dir;
    }

    private File getNotSecured(T item) {
        return new File(root, itemMapper.apply(item));
    }

    private void failOnNotExist(T item) {
        if (!isExist(item)) {
            throw new ItemNotFoundException();
        }
    }

    public void setRoot(File newRoot) {
        if (!newRoot.exists()) {
            files.createHiddenDir(newRoot);
        }
        root = newRoot;
    }

    public static class ItemNotFoundException extends InternalCloudException {
    }

    public static class ItemAlreadyExistsException extends InternalCloudException {
    }
}
