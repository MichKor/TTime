package pl.com.tt.ttime.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LdapCustomAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private LdapCustomUserDetailsContextMapper contextMapper;

    @Autowired
    public LdapCustomAuthoritiesPopulator(LdapCustomUserDetailsContextMapper contextMapper) {
        this.contextMapper = contextMapper;
    }

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations dirContextOperations, String username) {
        return contextMapper.mapUserFromContext(dirContextOperations, username, null).getAuthorities();
    }
}
