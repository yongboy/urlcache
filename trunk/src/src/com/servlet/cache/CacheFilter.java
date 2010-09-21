package com.servlet.cache;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.MapMaker;

public class CacheFilter implements Filter {
	private static final Log log = LogFactory.getLog(CacheFilter.class);

	public static final String HEADER_LAST_MODIFIED = "Last-Modified";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
	public static final String HEADER_EXPIRES = "Expires";
	public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

	// Cache Control
	public static final long MAX_AGE_NO_INIT = Long.MIN_VALUE;
	public static final long MAX_AGE_TIME = Long.MAX_VALUE;

	public static long EXPIRES_TIME = -1;

	private static final String REQUEST_FILTERED = "cache_filter_"
			+ CacheFilter.class.getName();

	private static ConcurrentMap<String, ResponseContent> cache = null;

	// Last Modified parameter
	protected static final long LAST_MODIFIED_INITIAL = -1;

	// Expires parameter
	protected static final long EXPIRES_ON = 1;

	private long time = 60;
	private long lastModified = LAST_MODIFIED_INITIAL;
	private long expires = EXPIRES_ON;
	private long cacheControlMaxAge = -60;

	private static Set<String> disableMethods = null;

	@Override
	public void init(FilterConfig config) throws ServletException {

		String timeStr = config.getInitParameter("time");

		if (StringUtils.isEmpty(timeStr)) {
			// 默认为一个小时
			timeStr = "3600";
		}

		EXPIRES_TIME = Long.parseLong(timeStr);

		// 初始化要缓存的请求方法
		String disableCacheOnMethods = config
				.getInitParameter("disableCacheOnMethods");

		if (StringUtils.isEmpty(disableCacheOnMethods)) {
			disableCacheOnMethods = "POST,DELETE,PUT";
		}

		disableMethods = new HashSet<String>(
				Arrays.asList(disableCacheOnMethods.split(",")));

		// 初始化缓存存储器
		cache = new MapMaker().concurrencyLevel(32).softValues()
				.expiration(EXPIRES_TIME, TimeUnit.SECONDS).makeMap();
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		log.info("servlet path : " + request.getServletPath());

		if (disableMethods.contains(request.getMethod())) {
			chain.doFilter(request, res);
			log.info("curr request method is not filtered!");
			return;
		}

		// 避免重复调用
		if (isFilteredBefore(request)) {
			chain.doFilter(request, res);
			log.info("curr request is filtered already!");
			return;
		}
		request.setAttribute(REQUEST_FILTERED, Boolean.TRUE);

		String key = getCacheKey(request);
		log.info("key : " + key);
		ResponseContent responseContent = cache.get(key);

		if (responseContent != null) {// 如果当前的URL已经有对应的响应内容
			log.info("now we use the cache content in our mem !");
			responseContent.writeTo(res);
			return;
		}

		CacheHttpServletResponseWrapper cacheResponse = new CacheHttpServletResponseWrapper(
				(HttpServletResponse) res, time * 1000L, lastModified, expires,
				cacheControlMaxAge);
		chain.doFilter(request, cacheResponse);
		cacheResponse.flushBuffer();

		if (cacheResponse.getStatus() == HttpServletResponse.SC_OK) {
			log.info("now the content is saved to the cache !");
			cache.put(key, cacheResponse.getContent());

			log.info("cache size : " + cache.size());
		} else {
			log.info("status is not 200 ok ! " + cacheResponse.getStatus());
		}
	}

	private String getCacheKey(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder(request.getRequestURI());

		String query = request.getQueryString();		
		if (query != null) {
			builder.append("_").append(query);
		}

		return builder.toString();
	}

	/**
	 * Checks if the request was filtered before, so guarantees to be executed
	 * once per request. You can override this methods to define a more specific
	 * behaviour.
	 * 
	 * @param request
	 *            checks if the request was filtered before.
	 * @return true if it is the first execution
	 */
	public boolean isFilteredBefore(ServletRequest request) {
		return request.getAttribute(REQUEST_FILTERED) != null;
	}

	@Override
	public void destroy() {
	}
}