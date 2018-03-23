package shenzhen.teamway.realm;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.ByteSource;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import shenzhen.teamway.bean.Resources;
import shenzhen.teamway.bean.User;
import shenzhen.teamway.service.ResourcesService;
import shenzhen.teamway.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MyShiroRealm extends AuthorizingRealm {
    @Autowired
    UserService userService;
    @Autowired
    ResourcesService resourcesService;
    //shiroredis集成的缓存系统
    @Autowired
    RedisSessionDAO redisSessionDAO;

    //对用户的授权操作
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        final User user = (User) SecurityUtils.getSubject().getPrincipal();
        final HashMap<Object, Object> map = new HashMap<>();
        map.put("userid", user.getId());
        final List<Resources> resources = resourcesService.loadResources(map);
        //权限信息对象
        final SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        for (Resources resource : resources) {
            info.addStringPermission(resource.getResurl());
        }
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //token只携带用户的账号名信息
        System.out.println(token.getPrincipal().toString());
        final String username = (String) token.getPrincipal();
        final User user = userService.selectByName(username);
        final SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(username), getName());
        //验证通过后放入session容器中
        final Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("userSession", user);
        session.setAttribute("userSessionId", user.getId());
        return info;
    }

    /*
    清理缓存,根据useid
     */
    public void clearSession(List<Integer> userid) {
        if (userid == null || userid.size() == 0) return;
        //获取所有session
        final Collection<Session> sessions = redisSessionDAO.getActiveSessions();
        final ArrayList<SimplePrincipalCollection> list = new ArrayList<>();
        for (Session session : sessions) {
            //强转
            Object obj = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (null != obj && obj instanceof SimplePrincipalCollection) {
                SimplePrincipalCollection sp = (SimplePrincipalCollection) obj;
                obj = sp.getPrimaryPrincipal();
                if (obj != null && obj instanceof User) {
                    User user = (User) obj;
                    System.out.println("user" + user);
                    if (user != null && userid.contains(user.getId())) {
                        list.add(sp);
                    }
                }
            }
        }
        final RealmSecurityManager manager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        final MyShiroRealm realm = (MyShiroRealm) manager.getRealms().iterator().next();
        for (SimplePrincipalCollection collection : list) {
                 realm.clearCachedAuthenticationInfo(collection);
        }
    }
}
