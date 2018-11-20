package com.smartsoft.csp.ssutil;


import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a path that may be used c multiple platforms. i.e. file systems (Mac, Windows, Unix) and web URLs
 */
public class Path implements Comparable<Path>, Serializable {

    public static final CharMatcher SCHEME_INVALID_CHARS = CharMatcher.noneOf("/\\.");

    public static final char BSLASH = '\\';
    public static final char FSLASH = '/';
    public static final char COLON = ':';

    public static final String SLASH = FSLASH + "";
    public static final String DOUBLE_SLASH = SLASH + SLASH;
    public static final String EMPTY_STRING = "";

    public static final char[] SLASHES = new char[]{BSLASH, FSLASH};

    public static final String HTTP_SCHEME = "http";
    public static final String FILE_SCHEME = "file";
    public static final String FTP_SCHEME = "ftp";

    private final static ImmutableSet<String> SCHEMES = ImmutableSet.of(FILE_SCHEME, HTTP_SCHEME, FTP_SCHEME);


    private final static Joiner JOINER = Joiner.on('/');
    public static final Path NULL_PATH = new Path();


    //will never have leading or trailing slashes
    private final String scheme;
    private final String path;


    public Path() {
        scheme = null;
        path = null;
    }

    public Path(String u) {
        if (isEmpty(u)) {
            scheme = null;
            path = null;
            return;
        }

        u = u.trim();

        //parse scheme
        int colon = u.indexOf(':');
        String p;
        if (colon == -1) {
            scheme = null;
            p = u;
        } else {
            //schema
            scheme = u.substring(0, colon).toLowerCase();
            p = u.substring(colon + 1);
        }

        path = fixPath(p);
    }

    public Path(Iterable segmentList) {
        this(JOINER.join(segmentList));
    }

    public Path(Path path1, Path path2) {
        if (path1 == null) {
            this.scheme = null;
            if (path2 == null) {
                this.path = null;
            } else {
                assert path2.scheme == null;
                this.path = path2.path;
            }
        } else {
            this.scheme = path1.scheme;
            if (path2 == null) {
                this.path = path1.path;
            } else {
                assert path2.scheme == null;
                this.path = path1.path + "/" + path2.path;
            }
        }
    }

    public Path(Path context, String pathString) {
        this(context, new Path(pathString));
    }

    public Path(String path1, String path2) {
        this(new Path(path1), new Path(path1));
    }

    private static void checkPath(String path) {
        path = nullNormalize(path);
        if (path == null) return;
        checkForSpaces(path);
    }

    private static String checkSegment(String segment) {
        checkNotNull(segment);
        segment = segment.trim();
        if (segment.contains(" ")) {
            throw new IllegalStateException("Failed containsSpace test for segment: [" + segment + "]");
        }
        return segment;
    }

    public boolean isNull() {
        return path == null;
    }

    private boolean isValid() {
        try {
            checkPath(path);
            return true;
        } catch (IllegalStateException e) {
//            return false;
            throw e;
        }
    }

    public static String fixUp(String path) {
        return fixPath(path);
    }

    public static String fixPath(String path) {
        path = nullNormalize(path);
        if (path == null) {
            return path;
        }

        checkPath(path);

        path = backSlashesToForwardSlashes(path);
        path = fixDoubleSlashes(path);
        path = trimLeadingSlashes(path);
        path = trimTrailingSlashes(path);

        return path;
    }


    public static String removeWindowsDriveLetterPrefix(String u) {

//        int xx = Ascii.

        char a = 'a';
        char z = 'z';


        ImmutableList.of("c", "d", "e");
        return u.replaceAll("c:", "");
    }


    public static String convertBackslashesToForward(String u) {
        return u.replaceAll("\\\\", "/");
    }

    public boolean hasHttpScheme() {
        return scheme != null && scheme.equals(HTTP_SCHEME);
    }


    public static boolean isValidSchemeName(String schemeName) {
        return SCHEMES.contains(schemeName);
    }


    public boolean isValidScheme(String schemeName) {
        return schemeName.contains(schemeName);
    }

    public static boolean isUrl(String s) {
        if (s.startsWith("http:/")) return true;
        else if (s.startsWith("file:/")) return true;
        else return false;
    }

    public boolean getPathString() {
        return isUrl(path);
    }


    public String toString(boolean leadingSlash, boolean trailingSlash) {
        assert isValid();
        if (path == null) {
            if (leadingSlash || trailingSlash) {
                return SLASH;
            } else {
                return EMPTY_STRING;
            }
        }

        StringBuilder a = new StringBuilder();

        if (leadingSlash && !hasScheme()) {
            a.append(FSLASH);
        }

        a.append(path);

        if (trailingSlash) {
            a.append(FSLASH);
        }

        return a.toString();
    }

    @Override
    public String toString() {
        return toString(true, false);
    }

    public String toStringNoLeadingSlash() {
        return toString(false, false);
    }

    public String toStringWithLeadingSlash() {
        return toString(true, false);
    }

    public static String trimTrailingSlash(String pathString) {
        if (pathString.endsWith("/")) return pathString.substring(0, pathString.length() - 1);
        return pathString + "";
    }

    public static String trimLeadingSlash(String s) {
        if (s.startsWith("/")) return s.substring(1);
        return s + "";
    }

    public Path copy() {
        return new Path(path);
    }

    public Path append(Path url) {
        if (url == null || url.isNull()) {
            return this;
        }
        return new Path(this, url);
    }

    public Path prepend(Path url) {
        return new Path(url, this);
    }

    public boolean isUrl() {
        return isUrl(path);
    }


    public static Path scheme(String scheme) {
        checkNotNull(scheme);
        scheme = scheme.trim();
        checkArgument(isValidSchemeName(scheme));
        return new Path(scheme + "://");
    }

    public static Path httpScheme() {
        return scheme("http");
    }

    public static Path domain(String domain) {
        checkNotNull(domain);
        domain = domain.trim();
        checkArgument(domain.indexOf('/') == -1);
        checkArgument(domain.indexOf('\\') == -1);
        return new Path(domain.trim());
    }

//    public static Path url(String scheme, String domain) {
//        Path s = Path.scheme(scheme);
//        Path d = Path.domain(domain);
//        return url(s, d);
//    }
//
//    public static Path url(Path scheme, Path domain) {
//        Path path = new Path(scheme, domain);
//        checkState(path.isUrl());
//        return path;
//    }


    public Path append(String url) {
        if (url == null) {
            return this;
        }
        return new Path(this, new Path(url));
    }

    public Path appendExt(String ext) {
        return appendName(ext);
    }

    public Path append(int url) {
        return new Path(this, new Path(url + ""));
    }

    public Path prepend(String url) {
        return new Path(new Path(url), this);
    }

    public Path appendName(String suffix) {
        String s = this.path + suffix;
        return new Path(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path that = (Path) o;

        assert this.isValid();
        assert that.isValid();
        if (!path.equals(that.path)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    public int compareTo(Path that) {
        if (that == null) throw new IllegalArgumentException("\"o\" is required");
        return this.path.compareTo(that.path);
    }

    public Path leftTrim(Path path) {
        String leftString = path.toString();
        String thisString = toString();
        return new Path(thisString.substring(leftString.length()));
    }

    public boolean endsWith(String s) {
        if (path == null) return false;
        return path.endsWith(s);
    }

    /**
     * up one folder
     * @return
     */
    public Path dotDot() {
        if (path == null) return this;

        int i = path.lastIndexOf('/');
        if (i == -1) return this;

        String s = path.substring(0, i);
        return new Path(s);
    }

    public boolean isHttpUrl() {
        if (path == null) {
            return false;
        } else {
            return path.toLowerCase().startsWith("http");
        }
    }


    public static Path nullPath() {
        return NULL_PATH;
    }

    public List<String> toList() {
        if (path == null) return ImmutableList.of();
        String[] a = path.split("/");
        return ImmutableList.copyOf(a);
    }

    public String getLocalName() {
        return getLast();
    }

    public String getLast() {
        List<String> list = toList();
        return list.get(list.size() - 1);
    }

    public String getFirst() {
        return get(0);
    }

    public String get(int index) {
        List<String> list = toList();
        return list.get(0);
    }

    public static boolean isEmpty(String path) {
        return Strings.isEmpty(path);
    }


    public static void checkForSpaces(String path) {
        if (path.contains(" ")) throw new IllegalStateException("Failed containsSpace test for path: [" + path + "]");
    }

    public static String nullNormalize(String path) {
        if (path == null) return null;
        path = path.trim();
        if (path.equals("")) return null;
        return path;
    }


    public static String backSlashesToForwardSlashes(String path) {
        path = nullNormalize(path);
        if (path == null) return null;
        return path.replace(BSLASH, FSLASH);
    }

    public static String fixDoubleSlashes(String path) {
        path = nullNormalize(path);
        if (path == null) return null;
        return path.replace(DOUBLE_SLASH, SLASH);
    }

    public static String trimLeadingSlashes(String path) {
        path = nullNormalize(path);
        if (path == null) return null;
        while (startsWithSlash(path)) {
            path = path.substring(1);
        }
        return path;
    }

    public static String trimTrailingSlashes(String path) {
        path = nullNormalize(path);
        if (path == null) return null;
        while (endsWithSlash(path)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static boolean startsWith(String path, char c) {
        return path.charAt(0) == c;
    }

    public static boolean endsWith(String path, char c) {
        return path.charAt(path.length() - 1) == c;
    }

    public static boolean startsWithSlash(String path) {
        return path.startsWith(path, BSLASH) || path.startsWith(path, FSLASH);
    }

    public static boolean endsWithSlash(String path) {
        return endsWith(path, BSLASH) || endsWith(path, FSLASH);
    }


    public boolean hasScheme() {
        return scheme != null;
    }

    public static final String[] EMPTY_SEGMENTS = new String[0];

    public String[] segments() {
        if (path == null) {
            return EMPTY_SEGMENTS;
        }
        return path.split(SLASH);
    }

    public String segmentAt(int index) {
        if (path == null) {
            throw new IndexOutOfBoundsException();
        }
        return segments()[index];
    }
}

