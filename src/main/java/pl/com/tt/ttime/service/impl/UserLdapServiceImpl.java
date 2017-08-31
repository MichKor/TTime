package pl.com.tt.ttime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.ldap.LdapCustomUserDetailsContextMapper;
import pl.com.tt.ttime.model.User;
import pl.com.tt.ttime.service.UserLdapService;

import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserLdapServiceImpl implements UserLdapService {

    @Autowired
    private DefaultSpringSecurityContextSource contextSource;

    @Value("${ttimemanager.ldap.get_users_query}")
    private String userQuery;

    @Override
    public List<User> queryUsers(String query) {
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        ldapTemplate.setDefaultCountLimit(10);

        String displayNameQuery;
        if (query == null || query.equals("")) {
            displayNameQuery = "*";
        } else {
            displayNameQuery = "*" + query + "*";
        }

        LdapQuery ldapQuery = LdapQueryBuilder.query().filter(String.format(userQuery, "*", displayNameQuery));
        List<User> userList = new ArrayList<>();
        ldapTemplate.search(ldapQuery, e -> {
            SearchResult searchResult = (SearchResult) e;
            Attributes attributes = searchResult.getAttributes();
            User user = LdapCustomUserDetailsContextMapper.mapUserFromAttributes(attributes);
            userList.add(user);
        });
        return userList;
    }

    @Override
    public User getUser(String username) {
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        ldapTemplate.setDefaultCountLimit(1);

        LdapQuery ldapQuery = LdapQueryBuilder.query().filter(String.format(userQuery, username, "*"));

        //Obejście wymagania, żeby zmienne przypisywane w lambdzie były final
        User[] userArray = new User[1];
        ldapTemplate.search(ldapQuery, e -> {
            SearchResult searchResult = (SearchResult) e;
            Attributes attributes = searchResult.getAttributes();
            userArray[0] = LdapCustomUserDetailsContextMapper.mapUserFromAttributes(attributes);
        });

        return userArray[0];
    }
}
