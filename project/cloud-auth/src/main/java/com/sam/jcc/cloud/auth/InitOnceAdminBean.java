/**
 *
 */
package com.sam.jcc.cloud.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;

/**
 * @author olegk
 */
@Component
@Slf4j
public class InitOnceAdminBean implements InitializingBean {

    private String rawPassword;

    public String getRawPassword() {
        return rawPassword;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String javaCloudHome = getProperty("javacloud.home");
        File hashedAdminFile = new File(javaCloudHome + File.separator + "sjc_admins.properties");
        if (!hashedAdminFile.exists()) {
            rawPassword = RandomStringUtils.random(10, true, true);
            log.warn("!!!!! THIS MESSAGE APPEARS ONLY ONCE !!! ADMIN PLAIN PASSWORD IS: " + rawPassword);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encoded = passwordEncoder.encode(rawPassword);
            Properties properties = new Properties();
            properties.setProperty("admin", encoded + ",ROLE_ADMIN");
            FileOutputStream fileOutputStream = new FileOutputStream(hashedAdminFile);
            properties.store(fileOutputStream, null);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
    }
}
