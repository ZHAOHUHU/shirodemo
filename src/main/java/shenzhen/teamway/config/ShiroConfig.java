package shenzhen.teamway.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shenzhen.teamway.bean.Resources;
import shenzhen.teamway.realm.MyShiroRealm;
import shenzhen.teamway.service.ResourcesService;

import java.util.LinkedHashMap;
import java.util.List;

/*
shiroconfig的配置文件
 */
@Configuration
public class ShiroConfig {

    @Value("192.168.159.135")
    private String host;
    @Value("6379")
    int port;
    @Value("2000")
    int timeout;
    @Value("<empty>")
    private String password;


    @Autowired
    ResourcesService resourcesService;

    /*
    filter生命周期
     */
    @Bean
    public static LifecycleBeanPostProcessor getLife() {
        return new LifecycleBeanPostProcessor();
    }

    /*
    shiroDialect，在thymeleaf里使用shiro标签
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * 注意：单独一个ShiroFilterFactoryBean配置是或报错的，因为在
     * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
     * <p>
     * Filter Chain定义说明
     * 1、一个URL可以配置多个Filter，使用逗号分隔
     * 2、当设置多个过滤器时，全部验证通过，才视为通过
     * 3、部分过滤器可指定参数，如perms，roles
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        final ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置securitymangager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/success");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        //自定义拦截器，从数据库动态获取url和权限
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();
        //加上一层匿名访问的过滤器就好了
        map.put("/login", "anon");
        map.put("/logout", "logout");
        map.put("/css/**", "anon");
        map.put("/js/**", "anon");
        map.put("/img/**", "anon");
        map.put("/font-awesome/**", "anon");
        //自定义加载权限关系
        final List<Resources> resources = resourcesService.queryAll();
        for (Resources resource : resources) {
            String permission = "perms[" + resource.getResurl() + "]";
            map.put(resource.getResurl(), permission);
        }
        map.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        final DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm
        securityManager.setRealm(shiroRealm());
        //自定义缓存
        securityManager.setCacheManager(cacheManager());
        //自定义session管理  使用redis
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    @Bean
    public MyShiroRealm shiroRealm() {
        final MyShiroRealm shiroRealm = new MyShiroRealm();
        shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return shiroRealm;
    }

    /*
    凭证匹配器
    * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     *  所以我们需要修改下doGetAuthenticationInfo中的代码;
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        final HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数
        return hashedCredentialsMatcher;
    }

    /*
    shiro的AOP注解
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        final AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /*
    shiro  redis的缓存支持
     */
    @Bean
    public RedisManager redisManager() {
        final RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setExpire(1800);
        redisManager.setTimeout(timeout);
        return redisManager;
    }

    /*
    cachemanager 缓存redis实现
    同样使用的是shiro-redis开源插件
     */
    public RedisCacheManager cacheManager() {
        final RedisCacheManager manager = new RedisCacheManager();
        manager.setRedisManager(redisManager());
        return manager;

    }

    /*
    redissessiondao shiro sessiondao 的实现使用的是shiro-redis开源插件
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        final RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    /*
    shiro session的管理
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        final DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        return sessionManager;
    }
}