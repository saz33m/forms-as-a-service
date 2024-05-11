package gov.gsa.form.service.config;

import com.github.ulisesbocchio.spring.boot.security.saml.bean.SAMLConfigurerBean;
import gov.gsa.form.service.security.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.ArrayList;
import java.util.List;

@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
@Slf4j
public class ServiceProviderConfig extends WebSecurityConfigurerAdapter {
    @Value("${security.saml2.metadata-url}")
    private String metadataUrl;

//    @Value("${server.ssl.key-alias}")
//    private String keyAlias;
//
//    @Value("${server.ssl.key-store-password}")
//    private String password;

    @Value("${port}")
    private String port;

    @Value("${host}")
    private String host;

//    @Value("${server.ssl.key-store}")
//    private String keyStoreFilePath;

    @Value("${security.saml2.entityId}")
    private String entityId;

    public ServiceProviderConfig() {
        super(false);
    }

    @Bean
    SAMLConfigurerBean samlConfig() {
        return new SAMLConfigurerBean();
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
        List<String> authnContext = new ArrayList<>();
        authnContext.add("http://idmanagement.gov/ns/assurance/aal/2");
        authnContext.add("http://idmanagement.gov/ns/assurance/ial/2");
        authnContext.add("http://idmanagement.gov/ns/requested_attributes?ReqAttr=email,phone,first_name,last_name,address1,address2,city,state,zipcode,phone,ssn");
        webSSOProfileOptions.setAuthnContexts(authnContext);
        // @formatter:off
        http.authorizeRequests()
            .antMatchers("/faas/saml*").permitAll()
            .and()
            .httpBasic()
            .disable()
            .csrf()
            .disable()
            .anonymous()
            .and()
            .apply(samlConfig())
            .serviceProvider()
            .metadataGenerator()
            .entityId(this.entityId)
            //  .bindingsSSO("artifact", "post", "paos")
            .and()
            .ecpProfile()
            .and()
            .sso()
            .profileOptions(webSSOProfileOptions)
            .defaultSuccessURL("/faas/ui")
//            .idpSelectionPageURL("/idpselection")
            .and()
            .authenticationProvider()
            .userDetailsService(new UserDetails())
            .and()
            .metadataManager()
            .metadataLocations(this.metadataUrl)
            .refreshCheckInterval(0)
            .and()
            .extendedMetadata()
            .ecpEnabled(true)
            .idpDiscoveryEnabled(false)//set to false for no IDP Selection page.
            .and()
            .keyManager()
//            .storeLocation("classpath:config/tls/keystore.p12")
            .privateKeyDERLocation("classpath:config/tls/localhost.key.der")
            .publicKeyPEMLocation("classpath:config/tls/localhost.cert")
            .and()
            .http()
            .authorizeRequests()
            .requestMatchers(samlConfig().endpointsMatcher()).permitAll()
            .and()
            .authorizeRequests()
            .anyRequest()
            .authenticated();
        // @formatter:on
    }
     // uncomment this to skip auth
//     @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http.cors().and().csrf().disable()
//            .exceptionHandling()
//            .and()
//            .headers()
//            .frameOptions()
//            .disable();
//   }
    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/index.html")
            .antMatchers("/test/**");
    }
}
