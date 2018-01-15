package com.oneops.infoblox.model.a;

import static com.oneops.infoblox.IBAEnvConfig.domain;
import static com.oneops.infoblox.IBAEnvConfig.isValid;
import static com.oneops.infoblox.IBAEnvConfig.nameServer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.oneops.infoblox.IBAEnvConfig;
import com.oneops.infoblox.InfobloxClient;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

/**
 * Address record tests.
 *
 * @author Suresh G
 */
@DisplayName("Infoblox address record tests.")
class ARecordTest {

  private static InfobloxClient client;

  private String fqdn = "oneops-test-arec1." + domain();

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

  /** Make sure to clean the host record before each test. */
  @BeforeEach
  void clean() throws IOException {
    client.deleteARec(fqdn);
  }

  @Test
  void create() throws IOException {
    List<ARec> aRec = client.getARec(fqdn);
    assertTrue(aRec.isEmpty());

    String ip = "10.10.10.30";
    ARec newARec = client.createARec(fqdn, ip);
    assertEquals(ip, newARec.ipv4Addr());
    assertEquals(Collections.singletonList(ip), digARecord(fqdn));

    client.deleteARec(fqdn);
  }

  /**
   * Queries default DNS server for the given fqdn and A record type.
   *
   * @param fqdn name.
   * @return list of mapped ip address strings.
   * @throws IOException
   */
  private List<String> digARecord(String fqdn) throws IOException {
    Lookup l = new Lookup(fqdn, Type.A);
    if (nameServer() != null) {
      l.setResolver(new SimpleResolver(nameServer()));
    }

    Record[] resolvedRecs = l.run();
    return resolvedRecs != null
        ? Arrays.stream(resolvedRecs)
            .map(ARecord.class::cast)
            .map(a -> a.getAddress().getHostAddress())
            .collect(Collectors.toList())
        : Collections.emptyList();
  }
}
