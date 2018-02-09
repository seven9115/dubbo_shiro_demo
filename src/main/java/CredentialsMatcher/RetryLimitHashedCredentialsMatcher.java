package CredentialsMatcher;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.PasswordMatcher;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


public class RetryLimitHashedCredentialsMatcher extends PasswordMatcher {

	private Ehcache passwordRetryCache;

	public RetryLimitHashedCredentialsMatcher() {
		CacheManager cacheManager = CacheManager.newInstance(CacheManager.class.getClassLoader().getResource("ehcache.xml"));
		passwordRetryCache = cacheManager.getCache("passwordRetryCache");
	}

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		String username = (String)token.getPrincipal();
		//retry count + 1
		Element element = passwordRetryCache.get(username);
		if(element == null) {
			element = new Element(username , new AtomicInteger(0));
			passwordRetryCache.put(element);
		}
		AtomicInteger retryCount = (AtomicInteger)element.getObjectValue();
		int i = retryCount.incrementAndGet();
		System.out.println(i);
		if(i > 5) {
			//if retry count > 5 throw
			throw new ExcessiveAttemptsException();
		}
		System.out.println(new Date().getTime());
		boolean matches = super.doCredentialsMatch(token, info);
		if(matches) {
			//clear retry count
			passwordRetryCache.remove(username);
		}
		return matches;
	}
}
