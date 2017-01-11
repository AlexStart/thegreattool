package com.sam.jcc.cloud.gallery;

import com.sam.jcc.cloud.utils.project.ArtifactIdValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.Lists.reverse;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.sam.jcc.cloud.gallery.MetadataResponse.response;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@RestController
@RequestMapping("/apps")
public class AppController {

    @Autowired
    ArtifactIdValidator nameValidator;

    Map<UUID, App> apps = newLinkedHashMap();

    {
        for (int i = 0; i < 100; i++) {
            final App app = new App();
            app.setId(UUID.randomUUID());
            app.setName("default-app-" + i);
            apps.put(app.getId(), app);
        }
    }

    @RequestMapping(value = "{id}", method = GET)
    public MetadataResponse<App> findById(@PathVariable UUID id) {
        return response(apps.get(id));
    }

    @RequestMapping(method = POST)
    public UUID create(@RequestBody App app) {
        nameValidator.validate(app.getName());

        final UUID id = UUID.randomUUID();
        app.setId(id);
        apps.put(id, app);
        return id;
    }

    @RequestMapping(method = GET)
    public List<MetadataResponse<App>> loadAll() {
        return apps.values()
                .stream()
                .map(MetadataResponse::response)
                .collect(toList());
    }

    @RequestMapping(value = "search", method = GET)
    public Page<MetadataResponse<App>> loadByPage(@PageableDefault Pageable page) {

        final List<MetadataResponse<App>> selected = reverse(apps.values().stream().collect(toList()))
                .subList(
                        page.getOffset(),
                        getFinishIndex(page)
                )
                .stream()
                .map(MetadataResponse::response)
                .collect(toList());

        return new PageImpl<>(selected, page, apps.size());
    }

    private int getFinishIndex(Pageable page) {
        final int size = apps.size();
        final int index = page.getOffset() + page.getPageSize();
        return index > size ? size : index;
    }

    @RequestMapping(value = "{id}", method = DELETE)
    public void remove(@PathVariable UUID id) {
        apps.remove(id);
    }

    @RequestMapping(method = PUT)
    public void update(@RequestBody App app) {
        nameValidator.validate(app.getName());
        apps.get(app.getId()).setName(app.getName());
    }
}
