package gov.gsa.form.service.security;

import gov.gsa.form.service.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named
@Slf4j
public class UserDetails  implements SAMLUserDetailsService {

    @Override
    public User loadUserBySAML(SAMLCredential credential)  {
        log.info("******* Parsing User Details, {}", credential.toString());
        HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String firstName = credential.getAttributeAsString("first_name");
        String lastName = credential.getAttributeAsString("last_name");
        String email = credential.getAttributeAsString("email");
        String address1 = credential.getAttributeAsString("address1");
        String city = credential.getAttributeAsString("city");
        String state = credential.getAttributeAsString("state");
        String zipcode = credential.getAttributeAsString("zipcode");
        String phone = credential.getAttributeAsString("phone");
        String ssn = credential.getAttributeAsString("ssn");

        User user = new User();
        if (StringUtils.isNotBlank(firstName)) {
            user.setFirstName(firstName);
        }
        if (StringUtils.isNotBlank(lastName)) {
            user.setLastName(lastName);
        }
        if (StringUtils.isNotBlank(email)) {
            user.setEmail(email);
        }
        if (StringUtils.isNotBlank(address1)) {
            user.setAddress1(address1);
        }
        if (StringUtils.isNotBlank(city)) {
            user.setCity(city);
        }
        if (StringUtils.isNotBlank(state)) {
            user.setState(state);
        }
        if (StringUtils.isNotBlank(zipcode)) {
            user.setZipcode(zipcode);
        }
        if (StringUtils.isNotBlank(phone)) {
            user.setPhone(phone);
        }
        if (StringUtils.isNotBlank(ssn)) {
            user.setSsn(ssn);
        }
        request.getSession().setAttribute("user", user);
        return user;
    }
}
