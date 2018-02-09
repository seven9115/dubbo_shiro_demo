package impl;

import Bean.UserBean;
import api.userApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class userImpl implements userApi {
	private final static ObjectMapper objectMapper = new ObjectMapper();


	Subject subject = SecurityUtils.getSubject();

	public String getName(String name) {

		System.out.println("get Name " + name);
		String subject = null;
		try {
			subject = Jwts.parser().setSigningKey(generateKey()).parseClaimsJws(name).getBody().getSubject();
		} catch (Exception e) {
			System.out.println("认证失败");
			return name;
		}
		UserBean userBean = null;
		try {
			userBean = objectMapper.readValue(subject, UserBean.class);
		} catch (Exception e) {
		}
		System.out.println(userBean);

		return name;
	}

	public String addName(UserBean user) {

		//Subject subject = SecurityUtils.getSubject();
		System.out.println(subject.isPermitted("user:update:1590"));

		return null;
	}

	public String Login(String name, String password) {

		System.out.println(name);
		System.out.println(password);

		//Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(name, password);
		try {
			// 4、登录，即身份验证
			subject.login(token);
			System.out.println("登录成功");
		} catch (AuthenticationException e) {
			System.out.println("登录失败");
			// 5、身份验证失败
		}

		// 计算token过期时间
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DATE, 0);
		Date expireTime = cal.getTime();

		UserBean userBean = new UserBean();
		userBean.setName(name);
		userBean.setPassword(password);
		String string = null;
		try {
			string = objectMapper.writeValueAsString(userBean);
		} catch (Exception e) {
		}
		String compactJws = Jwts.builder().setSubject(string)// 设置主题
				.setExpiration(expireTime).signWith(SignatureAlgorithm.HS512, generateKey())// 设置算法（必须）
				.compact();

		System.out.println(compactJws);
		System.out.println(subject.isPermitted("user:update:10998"));
		return null;
	}

	public static SecretKey generateKey() {
		// base64编码后做AES加密
		byte[] encodedKey = Base64.getDecoder().decode("hangzhou1gosun2isap"); //key的base64编码
		SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");//key的生成
		return key;
	}
}
