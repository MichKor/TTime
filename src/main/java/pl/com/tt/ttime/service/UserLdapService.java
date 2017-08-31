package pl.com.tt.ttime.service;

import pl.com.tt.ttime.model.User;

import java.util.List;

public interface UserLdapService {
    List<User> queryUsers(String query);

    User getUser(String username);
}
