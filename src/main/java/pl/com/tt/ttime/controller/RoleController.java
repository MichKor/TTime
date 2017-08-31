package pl.com.tt.ttime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.tt.ttime.model.Role;
import pl.com.tt.ttime.service.RoleService;

@RestController
@RequestMapping("/role")
public class RoleController extends AbstractController<Role, RoleService> {

    @Autowired
    public RoleController(RoleService service) {
        super(service);
    }
}
