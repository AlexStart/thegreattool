package com.sam.jcc.cloud;

/**
 * Created by Apanovich Alexander on 13.06.2017.
 */
public class PropertyResolverHelper {
    /*
     * Returns string for specified connection parameters.
     */
    public static String getConnectionUrl(String protocol, String host, String port) {
        String trimedProtocol = !isEmpty(protocol) ? protocol.trim() : "";
        String trimedHost = !isEmpty(host) ? host.trim() : "";;
        String trimedPort = !isEmpty(port) ? port.trim() : "";

        if (isEmpty(protocol) || isEmpty(host)) {
            throw new IllegalArgumentException("Connection parameters must not be empty.");
        }

        if (!isProtocolValid(trimedProtocol)) {
            throw new RuntimeException(String.format("The protocol format '%s' is invalid.", protocol));
        }
        if (!isHostValid(trimedHost)) {
            throw new RuntimeException(String.format("The host format '%s' is invalid.", host));
        }
        if (!isEmpty(trimedPort)) {
            if (!isPortValid(trimedPort)) {
                throw new RuntimeException(String.format("The port format '%s' is invalid.", port));
            }
        }
        return concatUrlParams(trimedProtocol, trimedHost, trimedPort);
    }

    /*
     * Returns true if specified string is a valid schema/protocol name.
     */
    public static boolean isProtocolValid(String protocol) {
        return !isEmpty(protocol) ? protocol.matches("^(ht|f)tp(s?)$|^(jdbc:mysql)$|^(mongo)$") : false;
    }

    /*
     * Returns true if specified string is a valid host name.
     */
    public static boolean isHostValid(String host) {
        return !isEmpty(host) ? host.matches("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$|(localhost)") : false;
    }

    /*
     * Returns true if specified string is a valid host number.
     */
    public static boolean isPortValid(String port) {
        return !isEmpty(port) ? port.matches("[0-9]+") : false;
    }

    /*
     * Returns true if specified string is null or empty.
     */
    private static boolean isEmpty(String param) {
        return (param == null || param.length() < 1);
    }

    /*
     * Returns string as concatenation of url params.
     */
    private static String concatUrlParams(String protocol, String host, String port) {
        StringBuilder strBuilder = new StringBuilder();
        return strBuilder.
                append(protocol).
                    append("://").
                        append(host).
                            append(isEmpty(port) ? "" : ":" + port).
                                append("/").toString();
    }

}
