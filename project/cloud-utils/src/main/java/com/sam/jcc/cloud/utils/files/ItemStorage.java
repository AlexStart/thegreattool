package com.sam.jcc.cloud.utils.files;

import com.sam.jcc.cloud.exception.InternalCloudException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
@Slf4j
public class ItemStorage<T> {

    @Getter
    private File root;

    private final Function<File, T> builder;
    private final Function<T, String> mapper;

    private final FileManager files = new FileManager();

    public ItemStorage(Function<T, String> mapper, Function<File, T> itemBuilder) {
        this.mapper = mapper;
        this.builder = itemBuilder;
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

    public List<T> getItems(){
        return files.getDirectoryFiles(root)
                .stream()
                .map(builder)
                .collect(toList());
    }

    private File getNotSecured(T item) {
        return new File(root, mapper.apply(item));
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
