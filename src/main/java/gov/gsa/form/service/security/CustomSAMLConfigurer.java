//package gov.gsa.form.service.security;
//
//import org.apache.commons.httpclient.HttpClient;
//import org.opensaml.Configuration;
//import org.opensaml.PaosBootstrap;
//import org.opensaml.saml2.metadata.provider.FilesystemMetadataProvider;
//import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
//import org.opensaml.saml2.metadata.provider.MetadataProvider;
//import org.opensaml.saml2.metadata.provider.MetadataProviderException;
//import org.opensaml.xml.ConfigurationException;
//import org.opensaml.xml.parse.ParserPool;
//import org.opensaml.xml.parse.StaticBasicParserPool;
//import org.opensaml.xml.parse.XMLParserException;
//import org.opensaml.xml.security.keyinfo.NamedKeyInfoGeneratorManager;
//import org.opensaml.xml.security.x509.CertPathPKIXTrustEvaluator;
//import org.opensaml.xml.security.x509.PKIXTrustEvaluator;
//import org.opensaml.xml.security.x509.X509KeyInfoGeneratorFactory;
//import org.springframework.core.io.DefaultResourceLoader;
//import org.springframework.core.io.Resource;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.ObjectPostProcessor;
//import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
//import org.springframework.security.extensions.saml2.config.SAMLDslEntryPoint;
//import org.springframework.security.saml.SAMLAuthenticationProvider;
//import org.springframework.security.saml.SAMLDiscovery;
//import org.springframework.security.saml.SAMLEntryPoint;
//import org.springframework.security.saml.SAMLProcessingFilter;
//import org.springframework.security.saml.context.SAMLContextProvider;
//import org.springframework.security.saml.context.SAMLContextProviderLB;
//import org.springframework.security.saml.key.JKSKeyManager;
//import org.springframework.security.saml.key.KeyManager;
//import org.springframework.security.saml.log.SAMLDefaultLogger;
//import org.springframework.security.saml.metadata.*;
//import org.springframework.security.saml.processor.*;
//import org.springframework.security.saml.trust.MetadataCredentialResolver;
//import org.springframework.security.saml.trust.PKIXInformationResolver;
//import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
//import org.springframework.security.saml.util.VelocityFactory;
//import org.springframework.security.saml.websso.WebSSOProfile;
//import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
//import org.springframework.security.saml.websso.WebSSOProfileImpl;
//import org.springframework.security.saml.websso.WebSSOProfileOptions;
//import org.springframework.security.web.DefaultSecurityFilterChain;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.access.channel.ChannelProcessingFilter;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import org.springframework.security.web.util.matcher.AndRequestMatcher;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
//import org.springframework.security.web.util.matcher.RequestMatcher;
//
//import javax.servlet.Filter;
//import javax.servlet.http.HttpServletRequest;
//import java.io.File;
//import java.io.IOException;
//import java.util.*;
//import java.util.regex.Pattern;
//
//public class CustomSAMLConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
//    private CustomSAMLConfigurer.IdentityProvider identityProvider = new CustomSAMLConfigurer.IdentityProvider();
//    private CustomSAMLConfigurer.ServiceProvider serviceProvider = new CustomSAMLConfigurer.ServiceProvider();
//    private WebSSOProfileOptions webSSOProfileOptions = this.webSSOProfileOptions();
//    private StaticBasicParserPool parserPool = this.staticBasicParserPool();
//    private SAMLProcessor samlProcessor = this.samlProcessor();
//    private SAMLDefaultLogger samlLogger = new SAMLDefaultLogger();
//    private SAMLAuthenticationProvider samlAuthenticationProvider;
//    private MetadataProvider metadataProvider;
//    private ExtendedMetadataDelegate extendedMetadataDelegate;
//    private CachingMetadataManager cachingMetadataManager;
//    private WebSSOProfile webSSOProfile;
//    private SAMLUserDetailsService samlUserDetailsService;
//    private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
//        @Override
//        public <T> T postProcess(T object) {
//            return object;
//        }
//    };
//
//    private CustomSAMLConfigurer() {
//    }
//
//    @Override
//    public void init(HttpSecurity http) {
//        this.metadataProvider = this.identityProvider.metadataProvider();
//        ExtendedMetadata extendedMetadata = this.extendedMetadata(this.identityProvider.discoveryEnabled);
//        this.extendedMetadataDelegate = this.extendedMetadataDelegate(extendedMetadata);
//        this.serviceProvider.keyManager = this.serviceProvider.keyManager();
//        this.cachingMetadataManager = this.cachingMetadataManager();
//        this.webSSOProfile = new WebSSOProfileImpl(this.samlProcessor, this.cachingMetadataManager);
//        this.samlAuthenticationProvider = this.samlAuthenticationProvider();
//        this.bootstrap();
//        SAMLContextProvider contextProvider = this.contextProvider();
//        SAMLEntryPoint samlEntryPoint = this.samlEntryPoint(contextProvider);
//
//        try {
//            http.httpBasic().authenticationEntryPoint(samlEntryPoint);
//            CsrfConfigurer<HttpSecurity> csrfConfigurer = (CsrfConfigurer)http.getConfigurer(CsrfConfigurer.class);
//            if (csrfConfigurer != null) {
//                RequestMatcher ignored = new AntPathRequestMatcher("/saml/SSO");
//                RequestMatcher notIgnored = new NegatedRequestMatcher(ignored);
//                RequestMatcher matcher = new AndRequestMatcher(new RequestMatcher[]{new CustomSAMLConfigurer.DefaultRequiresCsrfMatcher(), notIgnored});
//                csrfConfigurer.requireCsrfProtectionMatcher(matcher);
//            }
//        } catch (Exception var9) {
//            var9.printStackTrace();
//        }
//
//        http.addFilterBefore(this.metadataGeneratorFilter(samlEntryPoint, extendedMetadata), ChannelProcessingFilter.class).addFilterAfter(this.samlFilter(samlEntryPoint, contextProvider), BasicAuthenticationFilter.class).authenticationProvider(this.samlAuthenticationProvider);
//    }
//
//    public static CustomSAMLConfigurer saml() {
//        return new CustomSAMLConfigurer();
//    }
//
//    public CustomSAMLConfigurer userDetailsService(SAMLUserDetailsService samlUserDetailsService) {
//        this.samlUserDetailsService = samlUserDetailsService;
//        return this;
//    }
//
//    public CustomSAMLConfigurer.IdentityProvider identityProvider() {
//        return this.identityProvider;
//    }
//
//    public CustomSAMLConfigurer.ServiceProvider serviceProvider() {
//        return this.serviceProvider;
//    }
//
//    private String entityBaseURL() {
//        String entityBaseURL = this.serviceProvider.hostName + "/" + this.serviceProvider.basePath;
//        entityBaseURL = entityBaseURL.replace("//", "/").replaceAll("/$", "");
//        entityBaseURL = this.serviceProvider.protocol + "://" + entityBaseURL;
//        return entityBaseURL;
//    }
//
//    private SAMLEntryPoint samlEntryPoint(SAMLContextProvider contextProvider) {
//        SAMLEntryPoint samlEntryPoint = new SAMLDslEntryPoint();
//        samlEntryPoint.setDefaultProfileOptions(this.webSSOProfileOptions);
//        samlEntryPoint.setWebSSOprofile(this.webSSOProfile);
//        samlEntryPoint.setContextProvider(contextProvider);
//        samlEntryPoint.setMetadata(this.cachingMetadataManager);
//        samlEntryPoint.setSamlLogger(this.samlLogger);
//        return samlEntryPoint;
//    }
//
//    private SAMLProcessor samlProcessor() {
//        Collection<SAMLBinding> bindings = new ArrayList<>();
//        bindings.add(this.httpRedirectDeflateBinding(this.parserPool));
//        bindings.add(this.httpPostBinding(this.parserPool));
//        return new SAMLProcessorImpl(bindings);
//    }
//
//    private CachingMetadataManager cachingMetadataManager() {
//        List<MetadataProvider> providers = new ArrayList();
//        providers.add(this.extendedMetadataDelegate);
//        CachingMetadataManager cachingMetadataManager = null;
//
//        try {
//            cachingMetadataManager = new CachingMetadataManager(providers);
//        } catch (MetadataProviderException var4) {
//            var4.printStackTrace();
//        }
//
//        cachingMetadataManager.setKeyManager(this.serviceProvider.keyManager);
//        return cachingMetadataManager;
//    }
//
//    private StaticBasicParserPool staticBasicParserPool() {
//        StaticBasicParserPool parserPool = new StaticBasicParserPool();
//
//        try {
//            parserPool.initialize();
//        } catch (XMLParserException var3) {
//            var3.printStackTrace();
//        }
//
//        return parserPool;
//    }
//
//    private ExtendedMetadataDelegate extendedMetadataDelegate(ExtendedMetadata extendedMetadata) {
//        ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(this.metadataProvider, extendedMetadata);
//        extendedMetadataDelegate.setMetadataTrustCheck(false);
//        extendedMetadataDelegate.setMetadataRequireSignature(false);
//        return extendedMetadataDelegate;
//    }
//
//    private ExtendedMetadata extendedMetadata(boolean discoveryEnabled) {
//        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
//        extendedMetadata.setIdpDiscoveryEnabled(discoveryEnabled);
//        extendedMetadata.setSignMetadata(true);
//        return extendedMetadata;
//    }
//
//    private WebSSOProfileOptions webSSOProfileOptions() {
//        WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
//        // webSSOProfileOptions.setIncludeScoping(true);
////        webSSOProfileOptions.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
////        List<String> authnContext = new ArrayList<>();
////        authnContext.add("http://idmanagement.gov/ns/assurance/aal/2");
////        authnContext.add("http://idmanagement.gov/ns/assurance/ial/2");
////        authnContext.add("http://idmanagement.gov/ns/requested_attributes?ReqAttr=email,phone,first_name,last_name,address1,address2,city,state,zipcode,phone,ssn");
//        //    webSSOProfileOptions.setAuthnContexts(authnContext);
//        return webSSOProfileOptions;
//    }
//
//    private void bootstrap() {
//        try {
//            PaosBootstrap.bootstrap();
//        } catch (ConfigurationException var3) {
//            var3.printStackTrace();
//        }
//
//        NamedKeyInfoGeneratorManager manager = Configuration.getGlobalSecurityConfiguration().getKeyInfoGeneratorManager();
//        X509KeyInfoGeneratorFactory generator = new X509KeyInfoGeneratorFactory();
//        generator.setEmitEntityCertificate(true);
//        generator.setEmitEntityCertificateChain(true);
//        manager.registerFactory("MetadataKeyInfoGenerator", generator);
//    }
//
//    private HTTPPostBinding httpPostBinding(ParserPool parserPool) {
//        return new HTTPPostBinding(parserPool, VelocityFactory.getEngine());
//    }
//
//    private HTTPRedirectDeflateBinding httpRedirectDeflateBinding(ParserPool parserPool) {
//        return new HTTPRedirectDeflateBinding(parserPool);
//    }
//
//    private SAMLProcessingFilter samlWebSSOProcessingFilter(SAMLAuthenticationProvider samlAuthenticationProvider, SAMLContextProvider contextProvider, SAMLProcessor samlProcessor) throws Exception {
//        SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
//        AuthenticationManagerBuilder authenticationManagerBuilder = new AuthenticationManagerBuilder(this.objectPostProcessor);
//        authenticationManagerBuilder.authenticationProvider(samlAuthenticationProvider);
//        samlWebSSOProcessingFilter.setAuthenticationManager((AuthenticationManager)authenticationManagerBuilder.build());
//        samlWebSSOProcessingFilter.setContextProvider(contextProvider);
//        samlWebSSOProcessingFilter.setSAMLProcessor(samlProcessor);
//        return samlWebSSOProcessingFilter;
//    }
//
//    private MetadataGeneratorFilter metadataGeneratorFilter(SAMLEntryPoint samlEntryPoint, ExtendedMetadata extendedMetadata) {
//        MetadataGeneratorFilter metadataGeneratorFilter = new MetadataGeneratorFilter(this.getMetadataGenerator(samlEntryPoint, extendedMetadata));
//        metadataGeneratorFilter.setManager(this.cachingMetadataManager);
//        return metadataGeneratorFilter;
//    }
//
//    private FilterChainProxy samlFilter(SAMLEntryPoint samlEntryPoint, SAMLContextProvider contextProvider) {
//        List<SecurityFilterChain> chains = new ArrayList();
//        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"), new Filter[]{samlEntryPoint}));
//        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"), new Filter[]{new MetadataDisplayFilter()}));
//
//        try {
//            chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"), new Filter[]{this.samlWebSSOProcessingFilter(this.samlAuthenticationProvider, contextProvider, this.samlProcessor)}));
//        } catch (Exception var5) {
//            var5.printStackTrace();
//        }
//
//        SAMLDiscovery samlDiscovery = new SAMLDiscovery();
//        samlDiscovery.setMetadata(this.cachingMetadataManager);
//        samlDiscovery.setContextProvider(contextProvider);
//        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/discovery/**"), new Filter[]{samlDiscovery}));
//        return new FilterChainProxy(chains);
//    }
//
//    private SAMLAuthenticationProvider samlAuthenticationProvider() {
//        SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
//        samlAuthenticationProvider.setForcePrincipalAsString(false);
//        samlAuthenticationProvider.setSamlLogger(this.samlLogger);
//        samlAuthenticationProvider.setConsumer(new WebSSOProfileConsumerImpl());
//        samlAuthenticationProvider.setUserDetails(this.samlUserDetailsService);
//        return samlAuthenticationProvider;
//    }
//
//    private SAMLContextProvider contextProvider() {
//        SAMLContextProviderLB contextProvider = new SAMLContextProviderLB();
//        contextProvider.setMetadata(this.cachingMetadataManager);
//        contextProvider.setScheme(this.serviceProvider.protocol);
//        contextProvider.setServerName(this.serviceProvider.hostName);
//        contextProvider.setContextPath(this.serviceProvider.basePath);
//        contextProvider.setKeyManager(this.serviceProvider.keyManager);
//        MetadataCredentialResolver resolver = new MetadataCredentialResolver(this.cachingMetadataManager, this.serviceProvider.keyManager);
//        PKIXTrustEvaluator pkixTrustEvaluator = new CertPathPKIXTrustEvaluator();
//        PKIXInformationResolver pkixInformationResolver = new PKIXInformationResolver(resolver, this.cachingMetadataManager, this.serviceProvider.keyManager);
//        contextProvider.setPkixResolver(pkixInformationResolver);
//        contextProvider.setPkixTrustEvaluator(pkixTrustEvaluator);
//        contextProvider.setMetadataResolver(resolver);
//        return contextProvider;
//    }
//
//    private MetadataGenerator getMetadataGenerator(SAMLEntryPoint samlEntryPoint, ExtendedMetadata extendedMetadata) {
//        MetadataGenerator metadataGenerator = new MetadataGenerator();
//        metadataGenerator.setSamlEntryPoint(samlEntryPoint);
//        metadataGenerator.setEntityBaseURL(this.entityBaseURL());
//        metadataGenerator.setKeyManager(this.serviceProvider.keyManager);
//        metadataGenerator.setEntityId(this.serviceProvider.entityId);
//        metadataGenerator.setIncludeDiscoveryExtension(false);
//        metadataGenerator.setExtendedMetadata(extendedMetadata);
////        metadataGenerator.setRequestSigned(false);
////        metadataGenerator.setWantAssertionSigned(false);
//        return metadataGenerator;
//    }
//
//    private static final class DefaultRequiresCsrfMatcher implements RequestMatcher {
//        private Pattern allowedMethods;
//
//        private DefaultRequiresCsrfMatcher() {
//            this.allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
//        }
//
//        @Override
//        public boolean matches(HttpServletRequest request) {
//            return !this.allowedMethods.matcher(request.getMethod()).matches();
//        }
//    }
//
//    public class ServiceProvider {
//        private CustomSAMLConfigurer.ServiceProvider.KeyStore keyStore = new CustomSAMLConfigurer.ServiceProvider.KeyStore();
//        private KeyManager keyManager;
//        private String protocol;
//        private String hostName;
//        private String basePath;
//        private String entityId;
//
//        public ServiceProvider() {
//        }
//
//        public CustomSAMLConfigurer.ServiceProvider protocol(String protocol) {
//            this.protocol = protocol;
//            return this;
//        }
//
//        public CustomSAMLConfigurer.ServiceProvider hostname(String hostname) {
//            this.hostName = hostname;
//            return this;
//        }
//
//        public CustomSAMLConfigurer.ServiceProvider basePath(String basePath) {
//            this.basePath = basePath;
//            return this;
//        }
//
//        public CustomSAMLConfigurer.ServiceProvider entityId(String entityId) {
//            this.entityId = entityId;
//            return this;
//        }
//
//        public CustomSAMLConfigurer.ServiceProvider.KeyStore keyStore() {
//            return this.keyStore;
//        }
//
//        public CustomSAMLConfigurer and() {
//            return CustomSAMLConfigurer.this;
//        }
//
//        private KeyManager keyManager() {
//            DefaultResourceLoader loader = new DefaultResourceLoader();
//            Resource storeFile = loader.getResource(this.keyStore.getStoreFilePath());
//            Map<String, String> passwords = new HashMap();
//            passwords.put(this.keyStore.getKeyname(), this.keyStore.getKeyPassword());
//            return new JKSKeyManager(storeFile, this.keyStore.getPassword(), passwords, this.keyStore.getKeyname());
//        }
//
//        public class KeyStore {
//            private String storeFilePath;
//            private String password;
//            private String keyname;
//            private String keyPassword;
//
//            public KeyStore() {
//            }
//
//            public CustomSAMLConfigurer.ServiceProvider.KeyStore storeFilePath(String storeFilePath) {
//                this.storeFilePath = storeFilePath;
//                return this;
//            }
//
//            public CustomSAMLConfigurer.ServiceProvider.KeyStore password(String password) {
//                this.password = password;
//                return this;
//            }
//
//            public CustomSAMLConfigurer.ServiceProvider.KeyStore keyname(String keyname) {
//                this.keyname = keyname;
//                return this;
//            }
//
//            public CustomSAMLConfigurer.ServiceProvider.KeyStore keyPassword(String keyPasswordword) {
//                this.keyPassword = keyPasswordword;
//                return this;
//            }
//
//            public CustomSAMLConfigurer.ServiceProvider and() {
//                return ServiceProvider.this;
//            }
//
//            public String getStoreFilePath() {
//                return this.storeFilePath;
//            }
//
//            public String getPassword() {
//                return this.password;
//            }
//
//            public String getKeyname() {
//                return this.keyname;
//            }
//
//            public String getKeyPassword() {
//                return this.keyPassword;
//            }
//
//            @Override
//            public String toString() {
//                return "KeyStore{storeFilePath='" + this.storeFilePath + '\'' + ", password='" + this.password + '\'' + ", keyname='" + this.keyname + '\'' + ", keyPassword='" + this.keyPassword + '\'' + '}';
//            }
//
//            @Override
//            public boolean equals(Object o) {
//                if (this == o) {
//                    return true;
//                } else if (o != null && this.getClass() == o.getClass()) {
//                    CustomSAMLConfigurer.ServiceProvider.KeyStore keyStore = (CustomSAMLConfigurer.ServiceProvider.KeyStore)o;
//                    if (this.storeFilePath != null) {
//                        if (!this.storeFilePath.equals(keyStore.storeFilePath)) {
//                            return false;
//                        }
//                    } else if (keyStore.storeFilePath != null) {
//                        return false;
//                    }
//
//                    label46: {
//                        if (this.password != null) {
//                            if (this.password.equals(keyStore.password)) {
//                                break label46;
//                            }
//                        } else if (keyStore.password == null) {
//                            break label46;
//                        }
//
//                        return false;
//                    }
//
//                    if (this.keyname != null) {
//                        if (this.keyname.equals(keyStore.keyname)) {
//                            return this.keyPassword != null ? this.keyPassword.equals(keyStore.keyPassword) : keyStore.keyPassword == null;
//                        }
//                    } else if (keyStore.keyname == null) {
//                        return this.keyPassword != null ? this.keyPassword.equals(keyStore.keyPassword) : keyStore.keyPassword == null;
//                    }
//
//                    return false;
//                } else {
//                    return false;
//                }
//            }
//
//            @Override
//            public int hashCode() {
//                int result = this.storeFilePath != null ? this.storeFilePath.hashCode() : 0;
//                result = 31 * result + (this.password != null ? this.password.hashCode() : 0);
//                result = 31 * result + (this.keyname != null ? this.keyname.hashCode() : 0);
//                result = 31 * result + (this.keyPassword != null ? this.keyPassword.hashCode() : 0);
//                return result;
//            }
//        }
//    }
//
//    public class IdentityProvider {
//        private String metadataFilePath;
//        private boolean discoveryEnabled = true;
//
//        public IdentityProvider() {
//        }
//
//        public CustomSAMLConfigurer.IdentityProvider metadataFilePath(String metadataFilePath) {
//            this.metadataFilePath = metadataFilePath;
//            return this;
//        }
//
//        public CustomSAMLConfigurer.IdentityProvider discoveryEnabled(boolean discoveryEnabled) {
//            this.discoveryEnabled = discoveryEnabled;
//            return this;
//        }
//
//        private MetadataProvider metadataProvider() {
//            return (MetadataProvider)(this.metadataFilePath.startsWith("http") ? this.httpMetadataProvider() : this.fileSystemMetadataProvider());
//        }
//
//        private HTTPMetadataProvider httpMetadataProvider() {
//            try {
//                HTTPMetadataProvider httpMetadataProvider = new HTTPMetadataProvider(new Timer(), new HttpClient(), this.metadataFilePath);
//                httpMetadataProvider.setParserPool(CustomSAMLConfigurer.this.parserPool);
//                return httpMetadataProvider;
//            } catch (MetadataProviderException var2) {
//                var2.printStackTrace();
//                return null;
//            }
//        }
//
//        private FilesystemMetadataProvider fileSystemMetadataProvider() {
//            DefaultResourceLoader loader = new DefaultResourceLoader();
//            Resource metadataResource = loader.getResource(this.metadataFilePath);
//            File samlMetadata = null;
//
//            try {
//                samlMetadata = metadataResource.getFile();
//            } catch (IOException var7) {
//                var7.printStackTrace();
//            }
//
//            FilesystemMetadataProvider filesystemMetadataProvider = null;
//
//            try {
//                filesystemMetadataProvider = new FilesystemMetadataProvider(samlMetadata);
//            } catch (MetadataProviderException var6) {
//                var6.printStackTrace();
//            }
//
//            filesystemMetadataProvider.setParserPool(CustomSAMLConfigurer.this.parserPool);
//            return filesystemMetadataProvider;
//        }
//
//        public CustomSAMLConfigurer and() {
//            return CustomSAMLConfigurer.this;
//        }
//    }
//}
