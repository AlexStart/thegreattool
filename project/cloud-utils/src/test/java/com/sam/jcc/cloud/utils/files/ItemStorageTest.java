package com.sam.jcc.cloud.utils.files;

import com.sam.jcc.cloud.utils.files.ItemStorage.ItemAlreadyExistsException;
import com.sam.jcc.cloud.utils.files.ItemStorage.ItemNotFoundException;
import lombok.Data;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 30.12.2016
 */
public class ItemStorageTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    Item item;

    ItemStorage<Item> storage;

    @Before
    public void setUp() throws Exception {
        storage = new ItemStorage<>(
                Item::getName,
                f -> new Item(f.getName())
        );
        storage.setRoot(temp.newFolder());

        item = new Item("test-item");
    }

    @Test
    public void creates() {
        assertThat(storage.isExist(item)).isFalse();

        final File place = storage.create(item);
        assertThat(place)
                .exists()
                .isDirectory()
                .hasParent(storage.getRoot());

        assertThat(storage.isExist(item)).isTrue();
        assertThat(storage.get(item)).isEqualTo(place);
    }

    @Test
    public void deletes() {
        storage.create(item);
        storage.delete(item);
        assertThat(storage.isExist(item)).isFalse();
    }

    @Test
    public void getsItems() {
        assertThat(storage.getItems()).isEmpty();
        storage.create(item);
        assertThat(storage.getItems()).hasSize(1);
    }

    @Test(expected = ItemAlreadyExistsException.class)
    public void failsOnCreateDuplicate() {
        storage.create(item);
        storage.create(item);
    }

    @Test(expected = ItemNotFoundException.class)
    public void failsOnGetUnknown() {
        storage.get(item);
    }

    @Test(expected = ItemNotFoundException.class)
    public void failsOnDeleteUnknown() {
        storage.delete(item);
    }

    @Data
    static class Item {
        final String name;

        public Item(String name) {
            this.name = name;
        }
    }
}