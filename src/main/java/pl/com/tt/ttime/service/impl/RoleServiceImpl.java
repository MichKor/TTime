package pl.com.tt.ttime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.model.Role;
import pl.com.tt.ttime.repository.RoleRepository;
import pl.com.tt.ttime.service.RoleService;

@Service
public class RoleServiceImpl extends AbstractServiceImpl<Role, Long, RoleRepository> implements RoleService {

    @Autowired
    public RoleServiceImpl(RoleRepository repository) {
        super(repository);
    }

    @Override
    public Role findByName(String name) {
        return repository.findByName(name);
    }
}
