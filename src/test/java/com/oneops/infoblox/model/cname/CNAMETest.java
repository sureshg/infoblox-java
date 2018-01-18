package com.oneops.infoblox.model.cname;

import static com.oneops.infoblox.IBAEnvConfig.domain;
import static com.oneops.infoblox.IBAEnvConfig.isValid;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.oneops.infoblox.IBAEnvConfig;
import com.oneops.infoblox.InfobloxClient;
import com.oneops.infoblox.util.Dig;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.Type;

/**
 * Canonical record tests.
 *
 * @author Suresh G
 */
@DisplayName("Infoblox CNAME record tests.")
class CNAMETest {

  private static InfobloxClient client;

  private String canonicalName = "oneops-test." + domain();
  private String alias = "oneops-test-cname1." + domain();
  private String newAlias = "oneops-test-cname1-mod." + domain();

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

  /** Make sure to clean the CNAME record before each test. */
  @BeforeEach
  void clean() throws IOException {
    client.deleteCNameRec(alias);
    client.deleteCNameRec(newAlias);
  }

  @Test
  void create() throws IOException {
    List<CNAME> rec = client.getCNameRec(alias);
    assertTrue(rec.isEmpty());

    // Creates CNAME Record
    System.out.println("Creating alias " + alias + " for " + canonicalName);
    CNAME cname = client.createCNameRec(alias, canonicalName);
    assertEquals(cname.canonical(), canonicalName);
    // DNS lookup returns fqdn with dot at the end (as per the RFC).
    List<String> expected = singletonList(canonicalName + ".");
    assertEquals(expected, Dig.lookup(alias, Type.CNAME));

    // Modify CNAME Record
    List<CNAME> modCName = client.modifyCNameRec(alias, newAlias);
    assertTrue(modCName.size() == 1);
    // Now new Fqdn should resolve the IP.
    assertEquals(expected, Dig.lookup(newAlias, Type.CNAME));

    // Ideally the next assert should have succeeded, but usually DNS entries are cached
    // and take time to propagate the updates.
    // assertNotEquals(expected, Dig.lookup(alias, Type.CNAME));

    // Delete CNAME Record
    List<String> delCName = client.deleteCNameRec(alias);
    assertTrue(delCName.size() == 0);
    delCName = client.deleteCNameRec(newAlias);
    assertTrue(delCName.size() == 1);
  }
}
