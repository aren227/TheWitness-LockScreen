package com.aren.thewitnesspuzzle.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionComparator {

    private static Pattern pattern = Pattern.compile("v([\\d]+)\\.([\\d]+)");
    private static Pattern patternWithBeta = Pattern.compile("v([\\d]+)\\.([\\d]+)-beta");
    private static Pattern patternWithBetaVersion = Pattern.compile("v([\\d]+)\\.([\\d]+)-b([\\d]+)");

    /**
     * Compares two version strings.
     * A version string should follow below format:
     *  - vN.N
     *  - vN.N-beta
     *  - vN.N-bN
     *  Note that vN.N-b1 is newer version than vN.N-beta.
     *
     * @param a A version string
     * @param b A version string
     *
     * @return 1 if a is newer than b
     *         -1 if b is newer than a
     *         0 otherwise.
     **/
    public static int compare(String a, String b) {
        Version versionA = toVersionString(a);
        Version versionB = toVersionString(b);
        return versionA.compareTo(versionB);
    }

    private static Version toVersionString(String val) {
        try {
            Matcher matcher = pattern.matcher(val);
            if (matcher.matches())
                return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), false);

            matcher = patternWithBeta.matcher(val);
            if (matcher.matches())
                return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), true);

            matcher = patternWithBetaVersion.matcher(val);
            if (matcher.matches())
                return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
        } catch (Exception ignored) {

        }
        throw new IllegalArgumentException("Invalid version string");
    }

    private static class Version {
        int major, minor;
        boolean beta;
        int betaVersion;

        public Version(int major, int minor, boolean beta) {
            if (major < 0 || minor < 0)
                throw new IllegalArgumentException("major < 0 || minor < 0");
            this.major = major;
            this.minor = minor;
            this.beta = beta;
            if (beta)
                betaVersion = 0;
        }

        public Version(int major, int minor, int betaVersion) {
            if (major < 0 || minor < 0)
                throw new IllegalArgumentException("major < 0 || minor < 0");
            if (betaVersion <= 0)
                throw new IllegalArgumentException("betaVersion <= 0");
            this.major = major;
            this.minor = minor;
            this.beta = true;
            this.betaVersion = betaVersion;
        }

        public int compareTo(Version val) {
            if (major != val.major)
                return major > val.major ? 1 : -1;
            if (minor != val.minor)
                return minor > val.minor ? 1 : -1;
            if (beta != val.beta)
                return val.beta ? 1 : -1;
            if (betaVersion != val.betaVersion)
                return betaVersion > val.betaVersion ? 1 : -1;
            return 0;
        }
    }
}
