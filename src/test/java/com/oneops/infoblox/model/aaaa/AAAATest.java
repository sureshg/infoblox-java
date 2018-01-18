package com.oneops.infoblox.model.aaaa;

import static com.oneops.infoblox.IBAEnvConfig.domain;
import static com.oneops.infoblox.IBAEnvConfig.isValid;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.oneops.infoblox.IBAEnvConfig;
import com.oneops.infoblox.InfobloxClient;
import com.oneops.infoblox.util.Dig;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.Type;

/**
 * AAAA record tests.
 *
 * @author Suresh
 */
@DisplayName("Infoblox IPv6 address record tests.")
class AAAATest {

  private static InfobloxClient client;

  private String fqdn = "oneops-test-aaaa1." + domain();
  private String newFqdn = "oneops-test-aaaa1-mod." + domain();

  @BeforeAll
  static void setUp() {
    assumeTrue(isValid(), IBAEnvConfig::errMsg);
    client =
        InfobloxClient.builder()
            .endPoint(IBAEnvConfig.host())
            .userName(IBAEnvConfig.user())
            .password(IBAEnvConfig.password())
            .tlsVerify(false)
            .debug(true)
            .build();
  }

  /** Make sure to clean the AAAA record before each test. */
  @BeforeEach
  void clean() throws IOException {
    client.deleteAAAARec(fqdn);
    client.deleteAAAARec(newFqdn);
  }

  @Test
  void create() throws IOException {
    List<AAAA> aaaaRec = client.getAAAARec(fqdn);
    assertTrue(aaaaRec.isEmpty());

    // Creates AAAA Record
    String ipv6 = "fe80:0:0:0:f0ea:f6ff:fd97:5d51";
    AAAA newAAAARec = client.createAAAARec(fqdn, ipv6);
    assertNotNull(newAAAARec.ipv6Addr());
    assertEquals(Collections.singletonList(ipv6), Dig.lookup(fqdn, Type.AAAA));

    // Modify AAAA Record
    List<AAAA> modAAAARec = client.modifyAAAARec(fqdn, newFqdn);
    assertTrue(modAAAARec.size() == 1);
    // Now new Fqdn should resolve the IP.
    assertEquals(Collections.singletonList(ipv6), Dig.lookup(newFqdn, Type.AAAA));

    // Delete A Record
    List<String> delAAAARec = client.deleteAAAARec(fqdn);
    assertTrue(delAAAARec.size() == 0);
    delAAAARec = client.deleteAAAARec(newFqdn);
    assertTrue(delAAAARec.size() == 1);
  }
}
