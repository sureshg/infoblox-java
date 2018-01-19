package com.oneops.infoblox.util;

import static com.oneops.infoblox.IBAEnvConfig.domain;
import static com.oneops.infoblox.IBAEnvConfig.nameServer;
import static org.xbill.DNS.Type.A;
import static org.xbill.DNS.Type.AAAA;
import static org.xbill.DNS.Type.CNAME;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

/**
 * Domain server query utility. When a specific name server is not specified, it will use the
 * operating system's default resolver, usually configured via the resolv.conf file. By default it
 * queries the DNS root zone.
 *
 * @author Suresh G
 */
public class Dig {

  /**
   * Queries the DNS server for the given resource name and record type.
   *
   * <p>Warning: Writing test cases depending too much on DNS resolution might break the test cases,
   * as it usually cached and take time to propagate the DNS entries.
   *
   * @param fqdn name of the resource record that is to be looked up.
   * @param queryType {@link Type} indicates what type of query is required -- CNAME, A, MX, etc.
   * @return required answer list.
   * @throws IOException if a problem occurred talking to DNS.
   */
  public static List<String> lookup(String fqdn, int queryType) throws IOException {
    Lookup dig = new Lookup(fqdn, queryType);
    dig.setSearchPath(new String[] {domain()});
    dig.setResolver(new SimpleResolver(nameServer()));

    Record[] records = dig.run();
    List<String> answer = Collections.emptyList();

    if (records != null) {
      switch (queryType) {
        case A:
          answer =
              Arrays.stream(records)
                  .map(ARecord.class::cast)
                  .map(a -> a.getAddress().getHostAddress())
                  .collect(Collectors.toList());
          break;

        case AAAA:
          answer =
              Arrays.stream(records)
                  .map(AAAARecord.class::cast)
                  .map(a -> a.getAddress().getHostAddress())
                  .collect(Collectors.toList());
          break;

        case CNAME:
          answer =
              Arrays.stream(records)
                  .map(CNAMERecord.class::cast)
                  .map(c -> c.getTarget().toString())
                  .collect(Collectors.toList());
          break;

        default:
          throw new IllegalArgumentException("Unknown Query type: " + queryType);
      }
    }
    return answer;
  }
}
