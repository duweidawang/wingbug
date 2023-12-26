package com.example.dwbug.config;

import com.example.dwbug.dao.UserDao;
import com.example.dwbug.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Configuration
public class DecodePwdAuthenticationProvider extends DaoAuthenticationProvider {
    @Autowired
    UserDao userDao;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    StringRedisTemplate stringRedisTemplate;


    public DecodePwdAuthenticationProvider(UserDetailsServiceImpl userDetailsService){
        setUserDetailsService(userDetailsService);
    }


    @Override
        protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
            if (authentication.getCredentials() == null) {
                this.logger.debug("Failed to authenticate since no credentials provided");
                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            } else {

                String presentedPassword = authentication.getCredentials().toString();
                if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                    stringRedisTemplate.opsForValue().increment("passwordTime"+authentication.getPrincipal(),1);
                    stringRedisTemplate.expire("passwordTime"+authentication.getPrincipal(),1,TimeUnit.DAYS);

                    if(!Objects.isNull(stringRedisTemplate.opsForValue().get("passwordTime" + authentication.getPrincipal()))){
                        String s = stringRedisTemplate.opsForValue().get("passwordTime" + authentication.getPrincipal());
                        if(Integer.parseInt(s)%3==0){
                            stringRedisTemplate.opsForValue().set("freezeTime"+authentication.getPrincipal(),"1",1,TimeUnit.MINUTES);
                        }
                    }



                    this.logger.debug("Failed to authenticate since password does not match stored value");
                    throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
                }
            }
        }







}
