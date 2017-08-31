package pl.com.tt.ttime.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import pl.com.tt.ttime.ldap.LdapCustomAuthoritiesPopulator;
import pl.com.tt.ttime.ldap.LdapCustomUserDetailsContextMapper;

@Configuration
public class LdapAuthenticationConfig extends GlobalAuthenticationConfigurerAdapter {

    @Value("${ttimemanager.ldap.url}")
    private String ldapUrl;

    @Value("${ttimemanager.ldap.base}")
    private String ldapBase;

    @Value("${ttimemanager.ldap.userDn}")
    private String ldapUserDn;

    @Value("${ttimemanager.ldap.password}")
    private String ldapPassword;

    @Autowired
    private LdapCustomUserDetailsContextMapper ldapCustomUserDetailsContextMapper;

    @Autowired
    private LdapCustomAuthoritiesPopulator ldapCustomAuthoritiesPopulator;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userDnPatterns("sAMAccountName={0}")
                .userSearchFilter("sAMAccountName={0}")
                .contextSource(contextSource())
                .userDetailsContextMapper(ldapCustomUserDetailsContextMapper)
                .ldapAuthoritiesPopulator(ldapCustomAuthoritiesPopulator);
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(ldapUrl);
        contextSource.setBase(ldapBase);
        contextSource.setUserDn(ldapUserDn);
        contextSource.setPassword(ldapPassword);
        contextSource.setReferral("ignore");
        contextSource.afterPropertiesSet();
        return contextSource;
    }
}
