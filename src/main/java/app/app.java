package app;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class app {
	public static void main(String[] args) {
		/*ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[]{"META-INF/spring/spring_config.xml"});
		context.start();
		try {
			System.in.read();
		} catch (Exception e) {
		}*/
		com.alibaba.dubbo.container.Main.main(args);

	}
}