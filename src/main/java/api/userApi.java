package api;

import Bean.UserBean;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.*;

@Path("/user")
@Consumes({ContentType.APPLICATION_JSON_UTF_8})
@Produces({ContentType.APPLICATION_JSON_UTF_8})
public interface userApi {
	@GET
	String getName(@QueryParam("name")String name);

	@POST
	String addName(UserBean user);

	@GET
	@Path("/login")
	String Login(@QueryParam("name")String name,@QueryParam("password")String password);

}