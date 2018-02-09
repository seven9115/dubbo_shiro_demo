package Realm;

import com.alibaba.dubbo.container.spring.SpringContainer;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import Bean.User;
import Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

public class UserRealm extends AuthorizingRealm{
	String resource = "mybatis/configuration.xml";
	private PasswordService passwordService;
	public void setPasswordService(PasswordService passwordService) {
		this.passwordService = passwordService;
	}

	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
		}catch (Exception e){

		}
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		SqlSession session = sqlSessionFactory.openSession();
		UserMapper userMapper = session.getMapper(UserMapper.class);

		//UserMapper userMapper = SpringContainer.getContext().getBean(UserMapper.class);

		String username = (String) principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		authorizationInfo.setRoles(userMapper.findRoles(username));
		authorizationInfo.setStringPermissions(userMapper.findPermissions(username));
		System.out.println(userMapper.findPermissions(username));
		return authorizationInfo;
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
		}catch (Exception e){

		}
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		SqlSession session = sqlSessionFactory.openSession();
		UserMapper userMapper = session.getMapper(UserMapper.class);

		//UserMapper userMapper = SpringContainer.getContext().getBean(UserMapper.class);
		String username = (String) token.getPrincipal();
		User user = userMapper.findByUsername(username);
		if (user == null) {
			throw new UnknownAccountException();//没找到帐号
		}
		if (Boolean.TRUE.equals(user.getLocked())) {
			throw new LockedAccountException(); //帐号锁定
		}
		return new SimpleAuthenticationInfo(
				user.getUsername(), //用户名
				//passwordService.encryptPassword(user.getPassword()), //加密密码
				user.getPassword(), //密码
				ByteSource.Util.bytes(user.getCredentialsSalt()),//salt=username+salt
				getName()  //realm name
		);
	}
	@Override
	public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
		super.clearCachedAuthorizationInfo(principals);
	}

	@Override
	public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
		super.clearCachedAuthenticationInfo(principals);
	}

	@Override
	public void clearCache(PrincipalCollection principals) {
		super.clearCache(principals);
	}

	public void clearAllCachedAuthorizationInfo() {
		getAuthorizationCache().clear();
	}

	public void clearAllCachedAuthenticationInfo() {
		getAuthenticationCache().clear();
	}

	public void clearAllCache() {
		clearAllCachedAuthenticationInfo();
		clearAllCachedAuthorizationInfo();
	}


}
