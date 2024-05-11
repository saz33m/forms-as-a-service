package gov.gsa.form.service.service.impl;

import gov.gsa.form.service.dto.User;
import gov.gsa.form.service.payload.SignRequestPayload;
import gov.gsa.form.service.payload.Signers;
import gov.gsa.form.service.payload.builder.SignRequestPayloadBuilder;
import gov.gsa.form.service.service.SignRequestService;
import gov.gsa.form.service.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;


/**
 * @author Wube Kifle
 *
 * <p>
 * A service class to send a Sign request using Non Blocking HTTP Request
 * </p>
 */
@Named
@Slf4j
public class SignRequestServiceImpl implements SignRequestService {

    private static final String REDIRECT_URL_SIGNED = "/ui#/sign-success";
    private static final String REDIRECT_URL_NOT_SIGNED = "/ui#/sign-unsuccessful";
    private static final String HTTPS = "http://";
    private static final String FAAS = "/faas";
    private static final String USER = "user";

    @Value("${host}")
    private String host;

    @Value("${port}")
    private String port;

    @Value("${sign-request-token}")
    private String token;

    @Value("${sign-request-url}")
    private String signRequestUrl;

    private final WebClient webClient;

    private final HttpServletRequest request;

    public SignRequestServiceImpl(WebClient webClient, HttpServletRequest request) {
        this.webClient = webClient;
        this.request = request;
    }

    @Override
    public String executeSignRequest(String pdfUrl, String pdfName) {
        try {
            URL url = new URL(pdfUrl);
            byte[] encoded = encodePdfToByte(url);
            SignRequestPayload signRequestPayload = buildRequest(encoded, pdfName);
            String jsonString = ObjectMapperUtil.writeToJsonString(signRequestPayload);
            log.info("Sign Request JSON :{}", jsonString);
            return postRequest(jsonString).block();
        } catch (IOException e) {
            log.error("Error occurred executing SignRequest", e);
        }
        return "";
    }

    private SignRequestPayload buildRequest(byte[] encodedContent, String pdfName) {
         // String redirectUrl = String.format("%s%s:%s%s", HTTPS, this.host, this.port, FAAS);
       String redirectUrl = String.format("%s%s%s", HTTPS, this.host, FAAS);
        String urlToRedirectOnceSigned = redirectUrl + REDIRECT_URL_SIGNED;
        String urlToRedirectIfNotSigned = redirectUrl + REDIRECT_URL_NOT_SIGNED;
        User user = (User) request.getSession().getAttribute(USER);
        Signers signers = new Signers(user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail()
        );
        return SignRequestPayloadBuilder.builder()
            .fromEmail(user.getEmail())
            .fromEmailName(user.getFirstName())
            .redirectUrl(urlToRedirectOnceSigned)
            .redirectUrlDeclined(urlToRedirectIfNotSigned)
            .fileFromContent(encodedContent)
            .eventsCallbackUrl(redirectUrl)
            .fileFromContentName(pdfName)
            .signers(List.of(signers)).build();
    }

    private Mono<String> postRequest(String reqBody) {
        return webClient.post().uri(signRequestUrl)
            .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", "TOKEN " + token);
                    httpHeaders.add("Content-Type", "application/json");
                }
            )
            .body(Mono.just(reqBody), String.class)
            .retrieve().bodyToMono(String.class);
    }

    private byte[] encodePdfToByte(URL url) throws IOException {
        try (InputStream is = url.openStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] byteChunk = new byte[4096];
            int n;
            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
            return baos.toByteArray();
        }
    }
}
