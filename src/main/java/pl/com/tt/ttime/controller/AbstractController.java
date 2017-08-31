package pl.com.tt.ttime.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.com.tt.ttime.model.AbstractEntity;
import pl.com.tt.ttime.service.AbstractService;

import java.util.List;

public abstract class AbstractController<T extends AbstractEntity, S extends AbstractService<T, Long>> {
    protected S service;

    public AbstractController(S service) {
        this.service = service;
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping
    public @ResponseBody
    ResponseEntity<List<T>> listAll() {
        List<T> entities = service.listAll();

        if (entities.isEmpty()) {
            return new ResponseEntity<List<T>>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<T>>(entities, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping
    public @ResponseBody
    ResponseEntity<?> create(@RequestBody T obj) {
        T created = service.save(obj);

        return new ResponseEntity<T>(created, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{id}")
    public @ResponseBody
    ResponseEntity<T> get(@PathVariable Long id) {
        T entity = service.findById(id);

        if (entity == null) {
            return new ResponseEntity<T>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<T>(entity, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{id}")
    public @ResponseBody
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody T obj) {
        T entity = service.findById(id);

        if (entity == null) {
            return new ResponseEntity<T>(HttpStatus.NO_CONTENT);
        }

        obj.setId(id);
        obj = service.save(obj);

        return new ResponseEntity<T>(obj, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    public @ResponseBody
    ResponseEntity<?> delete(@PathVariable Long id) {
        T entity = service.findById(id);

        if (entity == null) {
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }

        service.delete(entity);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}