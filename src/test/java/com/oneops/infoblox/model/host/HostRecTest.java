package com.oneops.infoblox.model.host;

import static com.oneops.infoblox.IBAEnvConfig.domain;
import static com.oneops.infoblox.IBAEnvConfig.isValid;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.oneops.infoblox.IBAEnvConfig;
import com.oneops.infoblox.InfobloxClient;
import com.oneops.infoblox.model.SearchModifier;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Host record tests.
 *
 * <p>Note: If it's returns "AdmConDataError: None (IBDataConflictError: IB.Data.Conflict: The
 * action is not allowed. A parent was not found." error, that means you are using an invalid host
 * domain.
 *
 * @author Suresh G
 */
@DisplayName("Infoblox host record tests.")
class HostRecTest {

  private static InfobloxClient client;

  private final String fqdn = "oneops-test-host1." + domain();

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
   * Make sure to clean the host record before each test.
   */
  @BeforeEach
  void clean() throws IOException {
    client.deleteHostRec(fqdn);
  }

  @Test
  void create() throws IOException {
    List<Host> hostRec = client.getHostRec(fqdn);
    assertTrue(hostRec.isEmpty());

    hostRec = client.getHostRec(fqdn, SearchModifier.REGEX);
    assertTrue(hostRec.isEmpty());

    List<String> ipv4Addrs = Collections.singletonList("10.10.10.20");
    Host newHostRec = client.createHostRec(fqdn, ipv4Addrs);
    List<String> res =
        newHostRec.ipv4Addrs().stream().map(Ipv4Addrs::ipv4Addr).collect(Collectors.toList());
    assertEquals(ipv4Addrs, res);

    client.deleteHostRec(fqdn);
    hostRec = client.getHostRec(fqdn);
    assertTrue(hostRec.isEmpty());
  }
}
