package edu.hust.it4060.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ArgumentParserUtil {
    private static final Logger log = LoggerFactory.getLogger(ArgumentParserUtil.class);
    private static final String SHORT_KEY_PREFIX = "-";
    private static final String FULL_KEY_PREFIX = "--";
    
    private ArgumentParserUtil() {
    }
    
    public static class ArgumentParsingException extends RuntimeException {
        private final List<String> exceptions;
        
        public List<String> getExceptions() {
            return exceptions;
        }
        
        public ArgumentParsingException(List<String> exceptions) {
            super(exceptions.toString());
            this.exceptions = exceptions;
        }
        
    }
    
    /**
     * Contains information about whether a key is valid or an exception should be
     * raised.
     */
    public static class ParsingContext {
        // Map from short form (-p) to fully qualified form (--port)
        private final ImmutableMap<String, String> shortKeys;
        private final ImmutableSet<String> fullKeys;
        
        public ParsingContext(ImmutableMap<String, String> shortKeys, ImmutableSet<String> fullKeys) {
            this.shortKeys = shortKeys;
            this.fullKeys = ImmutableSet.<String> builder()
                .addAll(fullKeys)
                .addAll(shortKeys.values())
                .build();
        }
        
        /**
         * Get the full key for an input string (i.e "p" will returns "port"). If the
         * input string is already a full key, it will be returned ("port" returns
         * "port").
         * 
         * @return the full key, or {@code null} if not found
         */
        public String getFullKey(String input) {
            if (input == null) {
                return null;
            }
            if (fullKeys.contains(input)) {
                return input;
            }
            return shortKeys.get(input);
        }
        
        /**
         * @return whether input is a valid fullkeys
         */
        public boolean isFullKey(String input) {
            return fullKeys.contains(input);
        }
        
        /**
         * Return a parsing context that contains the followings key mapping:
         * <ol>
         * <li>h -> host</li>
         * <li>p -> port</li>
         * </ol>
         * 
         * @param extraShortKeys other key mappings
         * @param extraFullKeys  other full key that may or may not contains a short
         *                       handed form
         * @return a parsing context instance with all key mapping
         */
        public static ParsingContext createNetworkContext(Map<String, String> extraShortKeys, Set<String> extraFullKeys) {
            var sk = ImmutableMap.<String, String> builder()
                .putAll(extraShortKeys)
                .put("p", "port")
                .put("h", "host")
                .build();
            
            return new ParsingContext(sk, ImmutableSet.copyOf(extraFullKeys));
        }
    }
    
    public static NetworkArguments parseArguments(String[] args, ParsingContext context)
        throws ArgumentParsingException {
        
        List<String> errors = new ArrayList<>();
        Properties properties = new Properties();
        
        int position = 0;
        
        while (position < args.length) {
            String string = args[position++];
            
            String arg = getArg(position, string);
            String value = null;
            
            int i;
            if (arg == null) {
                errors.add("Can't parse argument %s".formatted(string));
            } else if ((i = arg.indexOf("=")) == 0) {
                arg = null;
                errors.add("Invalid position of '=' for %s".formatted(string));
            } else if (i > 0) {
                value = arg.substring(i + 1);
                arg = arg.substring(0, i);
            }
            
            String fullKey = context.getFullKey(arg);
            if (fullKey == null) {
                errors.add("Unknown argument flag %s".formatted(arg));
            }
            
            if (fullKey != null // We have a valid key
                && value == null // We didn't have the '=' in '--port=8080' so argValue is null
                && (position + 1 >= args.length // We are at the end of array
                    || args[position + 1].startsWith(FULL_KEY_PREFIX) // The next element is a flag/option
                    || args[position + 1].startsWith(SHORT_KEY_PREFIX))) {
                errors.add("Missing value for option %s".formatted(arg));
            } else if (fullKey != null && value == null) {
                properties.setProperty(fullKey, args[position++]);
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ArgumentParsingException(errors);
        }
        return NetworkArguments.from(properties);
    }
    
    private static String getArg(int position, String string) {
        if (string.startsWith(FULL_KEY_PREFIX)) {
            // --log-file-path
            return string.substring(FULL_KEY_PREFIX.length());
        } else if (string.startsWith(SHORT_KEY_PREFIX)) {
            // -l
            return string.substring(SHORT_KEY_PREFIX.length());
        } else if (position == 0) {
            // It is host
            return "host";
        } else if (position == 1) {
            // it is port
            return "port";
        }
        return null;
        
    }
    
    public static class NetworkArguments {
        
        private final InetAddress host;
        private final int port;
        private final Properties extra;
        
        public NetworkArguments(InetAddress host, int port, Properties properties) {
            this.host = host;
            this.port = port;
            this.extra = properties;
        }
        
        public InetAddress getHost() {
            return host;
        }
        
        public int getPort() {
            return port;
        }
        
        public String getArgument(String key, String fallback) {
            return extra.contains(key) ? extra.getProperty(key) : fallback;
        }
        
        public static NetworkArguments from(Properties properties) throws IllegalArgumentException {
            InetAddress inetAddress = InetAddress.getLoopbackAddress();
            int port = 8080;
            try {
                inetAddress = InetAddress.getByName((String) properties.remove("host"));
            } catch (UnknownHostException e) {
                log.warn("Can't parse InetAddress from command line, falling back to {}", inetAddress);
            }
            
            try {
                port = Integer.parseInt((String) properties.remove("port"));
            } catch (NumberFormatException e) {
                log.warn("Can't parse port from command line, falling back to {}", port);
            }
            
            return new NetworkArguments(inetAddress, port, properties);
        }
        
    }
}
