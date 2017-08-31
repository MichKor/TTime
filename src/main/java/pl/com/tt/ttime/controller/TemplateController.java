package pl.com.tt.ttime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.com.tt.ttime.model.Template;
import pl.com.tt.ttime.model.User;
import pl.com.tt.ttime.security.SecurityUtils;
import pl.com.tt.ttime.service.TemplateService;

import java.util.Objects;

@RestController
@RequestMapping("/template")
public class TemplateController extends AbstractController<Template, TemplateService> {

    @Autowired
    public TemplateController(TemplateService service) {
        super(service);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Template obj) {
        obj.setUser(SecurityUtils.getCurrentUser());
        return super.update(id, obj);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping
    @Override
    public ResponseEntity<?> create(@RequestBody Template obj) {
        obj.setUser(SecurityUtils.getCurrentUser());
        return super.create(obj);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<?> delete(@PathVariable Long id) {
        User user = SecurityUtils.getCurrentUser();
        Template entity = service.findById(id);

        if (entity == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        if (!entity.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<Object>(HttpStatus.FORBIDDEN);
        }

        service.delete(entity);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
