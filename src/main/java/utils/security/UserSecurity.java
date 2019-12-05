package utils.security;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itcast.pojo.Permission;
import com.itcast.pojo.Role;
import com.itcast.pojo.User;
import com.itcast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @version V1.0
 * @author: WangQingLong
 * @date: 2019/11/30 19:06
 * @description:
 */
@Component
public class UserSecurity implements UserDetailsService {

    //注意：此处要通过dubbo远程调用用户服务
    @Reference
    private UserService userService;

    //密文加密
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    /**
     * 根据用户名查询账户信息
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


//        User user = userService.findByUsername(username);
        User user = userService.findByUserNameByMyBatis(username);
        if (user == null) {
            //用户名不存在
            return null;
        }

        List<GrantedAuthority> list = new ArrayList<>();

        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                //授权
                list.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }


        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), list);
    }
}
