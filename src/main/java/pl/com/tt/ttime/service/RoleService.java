package pl.com.tt.ttime.service;

import pl.com.tt.ttime.model.Role;

public interface RoleService extends AbstractService<Role, Long> {
    Role findByName(String name);
}
