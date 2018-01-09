package com.oneops.infoblox;

/**
 * Commonly used DNS resource records (RRs) permissible in zone
 * files of the Domain Name System (DNS).
 *
 * @author Suresh G
 */
public enum DNSRecord {

  /**
   * Returns a 32-bit IPv4 address, most commonly used to map host-names to
   * an IP address of the host.
   */
  A(1, "Address record"),

  /**
   * Delegates a DNS zone to use the given authoritative name servers.
   */
  NS(2, "Name server record"),

  /**
   * Alias of one name to another: the DNS lookup will continue by retrying the
   * lookup with the new name.
   */
  CNAME(5, "Canonical name record"),

  /**
   * Pointer to a canonical name. Unlike a CNAME, DNS processing stops and just the
   * name is returned. The most common use is for implementing reverse DNS lookups.
   */
  PTR(12, "Pointer record"),

  /**
   * Maps a domain name to a list of message transfer agents (MTA) for that domain.
   */
  MX(15, "Mail exchange record"),

  /**
   * For arbitrary human/machine readable text in a DNS record.
   */
  TXT(16, "Text record"),

  /**
   * Returns a 128-bit IPv6 address, most commonly used to map host-names to
   * an IP address of the host.
   */
  AAAA(28, "IPv6 address record"),

  /**
   * Generalized service location record, used for newer protocols instead of creating
   * protocol-specific records such as MX.
   */
  SRV(33, "Service locator"),

  /**
   * Stores PKIX, SPKI, PGP, etc.
   */
  CERT(37, "Certificate record"),

  /**
   * Can be used for publishing mappings from host-names to URIs.
   */
  URI(256, "Uniform Resource Identifier");

  /**
   * DNS resource type id.
   */
  private final int id;

  /**
   * Resource type description.
   */
  private final String desc;

  DNSRecord(int id, String desc) {
    this.id = id;
    this.desc = desc;
  }

  /**
   * Returns the DNS resource description.
   *
   * @return description.
   */
  public String getDesc() {
    return desc;
  }

  /**
   * Returns the DNS resource type id.
   *
   * @return id.
   */
  public int getId() {
    return id;
  }
}
