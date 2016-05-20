package poc.samsung.fido.rp.util;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.google.common.net.HttpHeaders;
import com.samsung.sds.fido.uaf.message.transport.HttpStatusCode;
import com.samsung.sds.fido.uaf.message.transport.context.RpContext;
import com.samsung.sds.fido.uaf.server.sdk.http.HttpResponse;

public class Utils {

	private static final Map<String, RpContext> CONTEXT_CACHE = new HashMap<>();

	private static final Map<String, String> TRUSTED_FACETS_CACHE = new HashMap<>();

	private static final Map<String, List<String>> DEVICE_INFO_CACHE = new HashMap<>();

	private static final List<Pattern> passwordPatternList = new ArrayList<Pattern>();

	public static void putContext(String sessionId, RpContext context, int lifetimeMillis) {
		// TODO: use cache engine that support expiration time
		CONTEXT_CACHE.put(sessionId, context);
	}

	public static RpContext getContext(String sessionId) {
		return CONTEXT_CACHE.get(sessionId);
	}

	public static void removeContext(String sessionId) {
		CONTEXT_CACHE.remove(sessionId);
	}

	public static void putTrustedFacets(String rpId, String trustedFacets) {
		TRUSTED_FACETS_CACHE.put(rpId, trustedFacets);
	}

	public static String getTrustedFacets(String rpId) {
		return TRUSTED_FACETS_CACHE.get(rpId);
	}

	public static void removeTrustedFacets(String rpId) {
		TRUSTED_FACETS_CACHE.remove(rpId);
	}

	public static void putDeviceInfo(String mapKey, List<String> regIds) {
		DEVICE_INFO_CACHE.put(mapKey, regIds);
	}

	public static List<String> getDeviceInfo(String mapKey) {
		return DEVICE_INFO_CACHE.get(mapKey);
	}

	public static void removeDeviceInfo(String mapKey) {
		DEVICE_INFO_CACHE.remove(mapKey);
	}

	public static String getResult(HttpResponse httpResponse, HttpServletResponse servletResponse) {
		if (null == httpResponse) {
			servletResponse.setStatus(HttpStatusCode.HTTP_INTERNAL_SERVER_ERROR);
			return ApiConstants.EMPTY_BODY;
		}

		int statusCode = httpResponse.getStatusCode();
		String body = httpResponse.getBody();

		// TODO: handle more HTTP status code as needed
		switch (statusCode) {
		case HttpStatusCode.HTTP_OK:
		case HttpStatusCode.HTTP_ACCEPTED:
			return body;
		default:
			servletResponse.setStatus(HttpStatusCode.HTTP_INTERNAL_SERVER_ERROR);
			return body;
		}
	}

	public static String getSearchKey(String userName, String deviceId) {
		return userName + "$$" + deviceId;
	}

	public static void logRequest(Logger logger, HttpServletRequest request, String requestBody, String rpId,
			String apiKey) {
		String headerContentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
		String clientUserAgent = request.getHeader(HttpHeaders.USER_AGENT);
		String clientIp = request.getRemoteAddr();
		String bodyContent = null == headerContentType ? "" : new String(requestBody);

		List<Object> mList = new ArrayList<Object>();
		mList.add(clientIp);
		mList.add(headerContentType);
		mList.add(clientUserAgent);
		mList.add(bodyContent);
		mList.add(rpId);
		mList.add(apiKey);
		logger.trace(
				"action=logRequest, clientIp={}, Content-Type=\"{}\", User-Agent=\"{}\", body=\"{}\" rp=\"{}\" rpKey=\"{}\"",
				mList.toArray());
	}

	/*
	 * UUID 생성
	 */
	public static String generateUUID() {
		String uid = UUID.randomUUID().toString();
		return uid.replace("-", "");
	}

	/*
	 * 사내 proxy 설정
	 */
	public static void setProxy() {
		System.setProperty("https.proxyHost", "70.10.15.10");
		System.setProperty("https.proxyPort", "8080");
		System.setProperty("http.proxyHost", "70.10.15.10");
		System.setProperty("http.proxyPort", "8080");
	}

	public static String sha256HashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());
			byte byteData[] = md.digest();

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				String hex = Integer.toHexString(0xff & byteData[i]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	static {
		passwordPatternList.add(Pattern.compile("\\p{Digit}{1,}"));
		passwordPatternList.add(Pattern.compile("\\p{Punct}{1,}"));
		passwordPatternList.add(Pattern.compile("\\p{Upper}{1,}"));
		passwordPatternList.add(Pattern.compile("\\p{Lower}{1,}"));
	}

	public static boolean validatePasswordPatterns(String password) {
		for (Pattern p : passwordPatternList) {
			Matcher m = p.matcher(password);
			if (m.find() == false) {
				return false;
			}
		}
		return true;
	}
}
