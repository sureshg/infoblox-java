package com.oneops.infoblox;

/**
 * Env configuration for tests.
 *
 * @author Suresh G
 */
public class IBAEnvConfig {

  /** Check if valid inforblox env config available for testing. */
  public static boolean isValid() {
    return host() != null && user() != null && password() != null;
  }

  /** Returns IBA host of management interface. */
  public static String host() {
    return getEnv("iba_host");
  }

  /** IBA user name. */
  public static String user() {
    return getEnv("iba_user");
  }

  /** IBA user password */
  public static String password() {
    return getEnv("iba_password");
  }

  /**
   * IBA root domain (root label).
   *
   * <p>Warning: As per the RFC fully-qualified domain names (fqdn) end with a dot ('.'), but
   * infoblox somehow doesn't allow it as a valid domain name. If you use dot at the end, you will
   * get <b>IBDataConflictError: IB.Data.Conflict:Invalid domain name</b> or <b>A domain label
   * either starts or ends with an invalid character</b> error.
   *
   * @see <a href="http://www.dns-sd.org/trailingdotsindomainnames.html">Trailing Dots in Domain
   *     Names</a>
   */
  public static String domain() {
    return getEnv("iba_domain", "oneops.com");
  }

  /** Name server to query for testing. */
  public static String nameServer() {
    return getEnv("iba_nameserver");
  }

  /**
   * Returns the value of given env name by first looking into jvm system property with fall back to
   * system env.
   */
  private static String getEnv(String envName) {
    return System.getProperty(envName, System.getenv(envName));
  }

  private static String getEnv(String envName, String defValue) {
    String env = getEnv(envName);
    return env != null ? env : defValue;
  }

  /** Common error message for invalid env vars. */
  public static String errMsg() {
    return "Infoblox (IBA) env config not set. Skipping the tests.\n"
        + "In order to run the tests, set the following env vars\n"
        + " * iba_host        : Infoblox Hostname\n"
        + " * iba_user        : Infoblox Username\n"
        + " * iba_password    : Infoblox Password\n"
        + " * iba_domain      : Infoblox Domain name\n"
        + " * iba_nameserver  : Name server to query";
  }
}
