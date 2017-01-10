package com.sam.jcc.cloud.gallery;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.Maps.newHashMap;
import static com.sam.jcc.cloud.gallery.MetadataResponse.response;
import static java.util.stream.Collectors.toSet;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@RestController
@RequestMapping("/apps")
public class AppController {

    Map<UUID, App> apps = newHashMap();

    @RequestMapping(value = "{id}", method = GET)
    public MetadataResponse<App> findById(@PathVariable UUID id) {
        return response(apps.get(id));
    }

    @RequestMapping(method = POST)
    public UUID create(@RequestBody App app) {
        final UUID id = UUID.randomUUID();

        app.setId(id);
        apps.put(id, app);
        return id;
    }

    @RequestMapping(method = GET)
    public Collection<MetadataResponse<App>> loadAll() {
        return apps.values()
                .stream()
                .map(MetadataResponse::response)
                .collect(toSet());
    }

    @RequestMapping(value = "{id}", method = DELETE)
    public void remove(@PathVariable UUID id) {
        apps.remove(id);
    }

    @RequestMapping(method = PUT)
    public void update(@RequestBody App app) {
        apps.get(app.getId()).setName(app.getName());
    }
}
