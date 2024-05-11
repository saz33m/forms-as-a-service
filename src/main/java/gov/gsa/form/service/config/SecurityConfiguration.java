//package gov.gsa.form.service.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
////@Configuration
////@EnableWebSecurity
////@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
////@Import(SecurityProblemSupport.class)
////@Slf4j
//public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//    @Value("${security.saml2.metadata-url}")
//    private String metadataUrl;
//
//    @Value("${server.ssl.key-alias}")
//    private String keyAlias;
//
//    @Value("${server.ssl.key-store-password}")
//    private String password;
//
//    @Value("${port}")
//    private String port;
//
//    @Value("${host}")
//    private String host;
//
//    @Value("${server.ssl.key-store}")
//    private String keyStoreFilePath;
//
//    @Value("${security.saml2.entityId}")
//    private String entityId;
//
//    @Override
//    public void configure(WebSecurity web) {
//        web.ignoring()
//            .antMatchers(HttpMethod.OPTIONS, "/**")
//            .antMatchers("/app/**/*.{js,html}")
//            .antMatchers("/i18n/**")
//            .antMatchers("/content/**")
//            .antMatchers("/swagger-ui/index.html")
//            .antMatchers("/test/**");
//    }
//
//
////    @Override
////    protected void configure(final HttpSecurity http) throws Exception {
////        log.info(
////            "\n----------------------------------------------------------\n\t"
////                + "EntityId:\t{} \n\t"
////                + "Host:\t{} \n\t"
////                + "MetadataUrl:\t{}\n----------------------------------------------------------",
////            this.entityId, this.host, this.metadataUrl);
////
////        http
////            .csrf().disable()
////            .headers().frameOptions().sameOrigin()
////            .and()
////            .authorizeRequests()
////            .antMatchers("/faas/saml*").permitAll()
////            .anyRequest().authenticated()
////            .and()
////            .apply(saml())
////            .serviceProvider()
////            .entityId(this.entityId)
////            .keyStore()
////            .storeFilePath(this.keyStoreFilePath)
////            .password(this.password)
////            .keyname(this.keyAlias)
////            .keyPassword(this.password)
////            .and()
////            .protocol("https")
////            .hostname(String.format("%s", this.host))
////            // .hostname(String.format("%s:%S", this.host, this.port))
////            .basePath("/faas")
////            .and()
////            .userDetailsService(new UserDetails())
////            .identityProvider()
////            .metadataFilePath(this.metadataUrl);
////    }
//}
