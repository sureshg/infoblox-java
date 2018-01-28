package com.oneops.infoblox.model.a;

import static com.oneops.infoblox.IBAEnvConfig.domain;
import static com.oneops.infoblox.IBAEnvConfig.isValid;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * Address record tests.
 *
 * @author Suresh G
 */
@DisplayName("Infoblox address record tests.")
class ARecordTest {

  private static InfobloxClient client;

  private final String fqdn = "oneops-test-a1." + domain();
  private final String newFqdn = "oneops-test-a1-mod." + domain();

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

  /**
   * Make sure to clean the A record before each test.
   */
  @BeforeEach
  void clean() throws IOException {
    client.deleteARec(fqdn);
    client.deleteARec(newFqdn);
  }

  @Test
  void create() throws IOException {
    List<ARec> rec = client.getARec(fqdn);
    assertTrue(rec.isEmpty());

    // Creates A Record
    String ip = "10.10.10.22";
    ARec aRec = client.createARec(fqdn, ip);
    assertEquals(ip, aRec.ipv4Addr());
    assertEquals(Collections.singletonList(ip), Dig.lookup(fqdn, Type.A));

    // Modify A Record
    List<ARec> modifedARec = client.modifyARec(fqdn, newFqdn);
    assertEquals(1, modifedARec.size());
    // Now new Fqdn should resolve the IP.
    assertEquals(Collections.singletonList(ip), Dig.lookup(newFqdn, Type.A));

    // Delete A Record
    List<String> delARec = client.deleteARec(fqdn);
    assertEquals(0, delARec.size());
    delARec = client.deleteARec(newFqdn);
    assertEquals(1, delARec.size());
  }
}
