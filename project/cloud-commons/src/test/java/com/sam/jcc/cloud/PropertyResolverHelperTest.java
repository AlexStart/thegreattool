package com.sam.jcc.cloud;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by gladivs on 14.06.2017.
 */
public class PropertyResolverHelperTest {
    /*
     * The method validates format for http(s), jdbc:mysql and mongo schemas/protocols.
     */
    @Test
    public void protocolValidationTest() throws Exception {

        //**************** validate if protocol is empty ************************
        assertThat(PropertyResolverHelper.isProtocolValid(null)).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid(" ")).isFalse();

        //**************** http schema/protocol validation ************************
        assertThat(PropertyResolverHelper.isProtocolValid("=http")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("http:")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("http:/")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("http://")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("htp")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("http")).isTrue();

        //**************** http schema/protocol validation ************************
        assertThat(PropertyResolverHelper.isProtocolValid("=https")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("https:")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("https:/")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("https://")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("htps")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("https")).isTrue();

        //**************** http schema/protocol validation ************************
        assertThat(PropertyResolverHelper.isProtocolValid("=jdbc:mysql:")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("jdbc:mysql:")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("jdbc:mysql:/")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("jdbc:mysql://")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("jdbcmysql")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("jdbc mysql")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("jdbc-mysql")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("jdbc:mysql")).isTrue();

        //**************** http schema/protocol validation ************************
        assertThat(PropertyResolverHelper.isProtocolValid("=mongo:")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("mongo:")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("mongo:/")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("mongo://")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("mong")).isFalse();
        assertThat(PropertyResolverHelper.isProtocolValid("mongo")).isTrue();

    }

    /*
     * The method validates host format.
     */
    @Test
    public void hostValidationTest() throws Exception {

        //**************** validate if host is empty ************************
        assertThat(PropertyResolverHelper.isHostValid(null)).isFalse();
        assertThat(PropertyResolverHelper.isHostValid("")).isFalse();
        assertThat(PropertyResolverHelper.isHostValid(" ")).isFalse();

        //**************** host as name validation ************************
        assertThat(PropertyResolverHelper.isHostValid("://localhost")).isFalse();
        assertThat(PropertyResolverHelper.isHostValid("//localhost")).isFalse();
        assertThat(PropertyResolverHelper.isHostValid("/localhost")).isFalse();
        assertThat(PropertyResolverHelper.isHostValid("localhost:")).isFalse();
        assertThat(PropertyResolverHelper.isHostValid("localhost:8080")).isFalse();
        assertThat(PropertyResolverHelper.isHostValid("localhost:/")).isFalse();
        assertThat(PropertyResolverHelper.isHostValid("localhost/")).isFalse();
        assertThat(PropertyResolverHelper.isHostValid("local host")).isFalse();
        assertThat(PropertyResolverHelper.isHostValid("local-host")).isFalse();
        assertThat(PropertyResolverHelper.isHostValid("localhost")).isTrue();
        assertThat(PropertyResolverHelper.isHostValid("my.site-1.com")).isTrue();

        //**************** host as ip validation ************************
         assertThat(PropertyResolverHelper.isHostValid("://127.0.0.1")).isFalse();
         assertThat(PropertyResolverHelper.isHostValid("//127.0.0.1")).isFalse();
         assertThat(PropertyResolverHelper.isHostValid("/127.0.0.1")).isFalse();
         assertThat(PropertyResolverHelper.isHostValid("127.0.0.1:")).isFalse();
         assertThat(PropertyResolverHelper.isHostValid("127.0.0.1:8080")).isFalse();
         assertThat(PropertyResolverHelper.isHostValid("127.0.0.1:/")).isFalse();
         assertThat(PropertyResolverHelper.isHostValid("127.0.0.1/")).isFalse();
         assertThat(PropertyResolverHelper.isHostValid("127.0.0.1.1")).isFalse();
         assertThat(PropertyResolverHelper.isHostValid("127.0.1")).isFalse();
         assertThat(PropertyResolverHelper.isHostValid("127.0.0.1")).isTrue();
         assertThat(PropertyResolverHelper.isHostValid("192.168.99.100")).isTrue();
    }

    /*
     * The method validates port format.
     */
    @Test
    public void portValidationTest() throws Exception {

        //**************** validate if port is empty ************************
        assertThat(PropertyResolverHelper.isPortValid(null)).isFalse();
        assertThat(PropertyResolverHelper.isPortValid("")).isFalse();
        assertThat(PropertyResolverHelper.isPortValid(" ")).isFalse();

        //**************** port validation ************************
        assertThat(PropertyResolverHelper.isPortValid(":8080")).isFalse();
        assertThat(PropertyResolverHelper.isPortValid(":8080/")).isFalse();
        assertThat(PropertyResolverHelper.isPortValid("8080/")).isFalse();
        assertThat(PropertyResolverHelper.isPortValid("80A0")).isFalse();
        assertThat(PropertyResolverHelper.isPortValid("808*")).isFalse();
        assertThat(PropertyResolverHelper.isPortValid("8080A")).isFalse();
        assertThat(PropertyResolverHelper.isPortValid("-1000")).isFalse();
        assertThat(PropertyResolverHelper.isPortValid("8080")).isTrue();
    }

    /*
     * The method validates forming connection url.
     */
    @Test(expected = RuntimeException.class)
    public void getConnectionUrlTest() throws Exception {
        assertThat(PropertyResolverHelper.getConnectionUrl("jdbc:mysql", "localhost", "3306")).isEqualTo("jdbc:mysql://localhost:3306/");
        assertThat(PropertyResolverHelper.getConnectionUrl(" jdbc:mysql ", " localhost ", " 3306 ")).isEqualTo("jdbc:mysql://localhost:3306/");
        assertThat(PropertyResolverHelper.getConnectionUrl("jdbc:mysql", "localhost", "3306/"));
        assertThat(PropertyResolverHelper.getConnectionUrl("http", "localhost", "8082")).isEqualTo("http://localhost:8082/");
        assertThat(PropertyResolverHelper.getConnectionUrl("http", "localhost", null)).isEqualTo("http://localhost/");
    }

}
