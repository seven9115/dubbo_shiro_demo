package Mapper;

import Bean.User;

import java.util.Set;

public interface UserMapper {
	public Set<String> findRoles(String username);// 根据用户名查找其角色
	public Set<String> findPermissions(String username);// 根据用户名查找其权限
	public User findByUsername(String username); //根据用户名查找用户
}
