package com.sam.jcc.cloud.app;

import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.persistence.data.EntityNotFoundException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Alexey Zhytnik
 * @since 21-Jan-17
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AppMetadataDaoTest {

    @Autowired
    AppMetadataDao appMetadataDao;

    AppMetadata app = appTemplate();

    @After
    public void tearDown() {
        appMetadataDao
                .findAll()
                .stream()
                .map(this::asApp)
                .forEach(appMetadataDao::delete);
    }

    @Test
    public void creates() {
        final AppMetadata created = asApp(appMetadataDao.create(app));
        assertThat(created).isNotNull();
    }

    @Test(expected = JpaSystemException.class)
    public void storesOnlyUniqueApps() {
        appMetadataDao.create(appTemplate());
        appMetadataDao.create(appTemplate());
    }

    @Test
    public void reads() {
        appMetadataDao.create(app);

        final AppMetadata stored = asApp(appMetadataDao.read(appTemplate()));
        assertThat(stored).isEqualTo(app);
    }

    @Test
    public void deletes() {
        appMetadataDao.create(app);
        appMetadataDao.delete(app);

        assertThat(appMetadataDao.findAll()).isEmpty();
    }

    @Test
    public void updates() {
        appMetadataDao.create(app);
        appMetadataDao.update(app);
    }

    @Test(expected = EntityNotFoundException.class)
    public void failsOnReadUnknown() {
        appMetadataDao.read(app);
    }

    AppMetadata appTemplate() {
        final AppMetadata app = new AppMetadata();
        app.setProjectName("xproject");
        return app;
    }

    AppMetadata asApp(IAppMetadata app) {
        return (AppMetadata) app;
    }
}
