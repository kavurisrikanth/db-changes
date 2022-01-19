package d3e.core;

import java.net.URI;
//import org.springframework.boot.autoconfigure.http;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.ajax4jsf.resource.UserResource.UriData;
public class UriExt {

	static int _COLON = 0x3A;
	static int _LEFT_BRACKET = 0x5B;
	static int _RIGHT_BRACKET = 0x5D;
	public static URI http(URI uri, String authority, String unencodedPath, Map<String, String> queryParameters) {
		String userInfo = "";
		String host = "";
		int port = -1;

		if (authority != null && !authority.isEmpty()) {
			var hostStart = 0;
			// Split off the user info.
			boolean hasUserInfo = false;
			for (int i = 0; i < authority.length(); i++) {
				final int atSign = 0x40;
				if (authority.codePointAt(i) == atSign) {
					hasUserInfo = true;
					userInfo = authority.substring(0, i);
					hostStart = i + 1;
					break;
				}
			}
			var hostEnd = hostStart;
			if (hostStart < authority.length() && authority.codePointAt(hostStart) == _LEFT_BRACKET) {
				// IPv6 host.
				for (; hostEnd < authority.length(); hostEnd++) {
					if (authority.codePointAt(hostEnd) == _RIGHT_BRACKET)
						break;
				}
				if (hostEnd == authority.length()) {
					throw new IllegalArgumentException("Invalid IPv6 host entry.");
				}
				parseIPv6Address(authority, hostStart + 1, hostEnd);
				hostEnd++; // Skip the closing bracket.
				if (hostEnd != authority.length() && authority.codePointAt(hostEnd) != _COLON) {
					throw new IllegalArgumentException("Invalid end of authority");
				}
			}
			// Split host and port.
			boolean hasPort = false;
			for (; hostEnd < authority.length(); hostEnd++) {
				if (authority.codePointAt(hostEnd) == _COLON) {
					String portString = authority.substring(hostEnd + 1);
					// We allow the empty port - falling back to initial value.
					if (!portString.isEmpty())
						port = Integer.parseInt(portString);
					break;
				}
			}
			host = authority.substring(hostStart, hostEnd);
		}
		try {
			return new URI(uri.getScheme(), userInfo, host, port, uri.getPath(), uri.getQuery(), uri.getFragment());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;
	}

	public static URI file(String path, boolean windows) {
		// TODo
		return null;
	}
	public static String getBase() {
		// TODo
		return null;
	}
	public static URI directory(String path, boolean windows) {
		// TODO
		return null;
	}

	public static URI dataFromString(String content, String mimeType, Encoding2 encoding,
			Map<String, String> parameters, boolean base64) {
		// TODO
		return null;
	}

	public static URI dataFromBytes(List<Integer> bytes, String mimeType, Map<String, String> parameters,
			boolean percentEncoded) {
		// TODO
		return null;
	}

	public static URI https(String authority, String unencodedPath, Map<String, String> queryParameters) {
		// TODO
		return null;
	}

	public static URI getBase(URI uri) {
		return uri;
	}

	public static boolean getHasScheme(URI uri) {

		return uri.isAbsolute();
	}

	public static boolean getHasAuthority(URI uri) {
		if (uri.getAuthority() != null)
			return true;
		else
			return false;
	}

	public static boolean getHasPort(URI uri) {
		if (uri.getPort() == -1)
			return false;
		else
			return true;
	}

	public static boolean getHasQuery(URI uri) {
		if (uri.getQuery() != null)
			return true;
		else
			return false;
	}

	public static boolean getHasFragment(URI uri) {
		if (uri.getRawFragment() != null)
			return true;
		else
			return false;
	}

	public static boolean getHasEmptyPath(URI uri) {
		if (uri.getRawPath() != null)
			return true;
		else
			return false;

	}

	public static boolean getHasAbsolutePath(URI uri) {
		return uri.isOpaque();
	}

	public boolean isScheme(URI uri, String scheme) {

		return uri.getScheme().contains(scheme);
	}

	public static Iterable<String> getPathSegmentsiterable(URI uri) {
		return null;
	}

	public static List<String> getPathSegments(URI uri) {
		List<String> str = new ArrayList<String>();
		String ps = uri.getPath();
		int ns = needsNormalization(ps); // Number of segments
		if (ns < 0)
			// Nope -- just return it
			return str;
		char[] path = ps.toCharArray(); // Path in char-array form

		// Split path into segments
		int[] segs = new int[ns];
		split(path, segs);
		int i = 0;
		while (i < path.length - 1) {
			int j = i;
			while (path[i] != '\0') {
				i++;
			}
			String result2 = new String(path, j, i);
			str.add(result2);
		}

		return str;
	}

	public static Map<String, String> getQueryParameters(URI uri) {
		// TODO
		return null;
	}

	public static Map<String, Object> getQueryParametersobject(URI uri) {
		// TODO
		return null;
	}

	public static Map<String, List<String>> getQueryParametersAll(URI uri) {
		// TODO
		return null;
	}

	public static String getOrigin(URI uri) {
		// TODO
		return null;
	}

	public String toFilePath(boolean windows) {
		// TODO
		return null;
	}

	public URI replace(String scheme, String userInfo, String host, Integer port, String path,
			Iterable<String> pathSegments, String query, Map<String, Object> queryParameters, String fragment) {
		return null;
	}

	public static URI replace(String scheme, String userInfo, String host, Integer port, String path,
			Iterable<String> pathSegments, String query, Map<String, Object> queryParameters) {
		return null;
	}

	public URI removeFragment(URI uri) {
		if (uri.getFragment() != null) {
			return replace(uri.getScheme(), uri.getRawUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(),
					getPathSegmentsiterable(uri), uri.getQuery(), getQueryParametersobject(uri));
		} else {
			return uri;
		}
	}

	private static void split(char[] path, int[] segs) {
		int end = path.length - 1; // Index of last char in path
		int p = 0; // Index of next char in path
		int i = 0; // Index of current segment

		// Skip initial slashes
		while (p <= end) {
			if (path[p] != '/')
				break;
			path[p] = '\0';
			p++;
		}

		while (p <= end) {

			// Note start of segment
			segs[i++] = p++;

			// Find beginning of next segment
			while (p <= end) {
				if (path[p++] != '/')
					continue;
				path[p - 1] = '\0';

				// Skip redundant slashes
				while (p <= end) {
					if (path[p] != '/')
						break;
					path[p++] = '\0';
				}
				break;
			}
		}

		if (i != segs.length)
			throw new InternalError(); // ASSERT
	}

	private static int needsNormalization(String path) {
		boolean normal = true;
		int ns = 0; // Number of segments
		int end = path.length() - 1; // Index of last char in path
		int p = 0; // Index of next char in path

		// Skip initial slashes
		while (p <= end) {
			if (path.charAt(p) != '/')
				break;
			p++;
		}
		if (p > 1)
			normal = false;

		// Scan segments
		while (p <= end) {

			// Looking at "." or ".." ?
			if ((path.charAt(p) == '.') && ((p == end) || ((path.charAt(p + 1) == '/')
					|| ((path.charAt(p + 1) == '.') && ((p + 1 == end) || (path.charAt(p + 2) == '/')))))) {
				normal = false;
			}
			ns++;

			// Find beginning of next segment
			while (p <= end) {
				if (path.charAt(p++) != '/')
					continue;

				// Skip redundant slashes
				while (p <= end) {
					if (path.charAt(p) != '/')
						break;
					normal = false;
					p++;
				}

				break;
			}
		}

		return normal ? -1 : ns;
	}

	static List<Integer> parseIPv4Address(String host) {
		// Todo
		return null;
	}

	static List<Integer> parseIPv6Address(String host, Integer start, Integer end) {
		// Todo
		return null;
	}
}
