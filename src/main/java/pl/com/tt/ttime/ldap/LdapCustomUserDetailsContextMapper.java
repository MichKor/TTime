package pl.com.tt.ttime.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.model.User;
import pl.com.tt.ttime.service.UserService;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.Collection;

@Service
public class LdapCustomUserDetailsContextMapper implements UserDetailsContextMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapCustomUserDetailsContextMapper.class);

    private UserService userService;

    @Autowired
    public LdapCustomUserDetailsContextMapper(UserService userService) {
        this.userService = userService;
    }

    public static User mapUserFromAttributes(Attributes attributes) {
        try {
            User user = new User();
            user.setUsername(attributes.get("sAMAccountName").get().toString());
            user.setEmail(attributes.get("mail").get().toString());
            user.setDisplayName(attributes.get("cn").get().toString());
            Attribute langAttribute = attributes.get("msexchuserculture");
            if (langAttribute != null) {
                String lang = langAttribute.get().toString();
                if (lang != null && lang.contains(",")) {
                    String[] langs = lang.split(",");
                    lang = langs[0];
                }
                user.setLocaleName(lang);
            }
            return user;
        } catch (NamingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations context, String userName, Collection<? extends GrantedAuthority> collection) {
        return userService.findOrCreateNewUser(mapUserFromAttributes(context.getAttributes()));
    }

    @Override
    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
        //To chyba nie jest potrzebne
    }
}
