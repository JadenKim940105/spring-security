package me.summerbell.springsecurity.form;

import me.summerbell.springsecurity.account.Account;
import me.summerbell.springsecurity.account.AccountContext;
import me.summerbell.springsecurity.common.SecurityLogger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SampleSerivce {

    public void dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        //Account account = AccountContext.getAccount();
        System.out.println("==========");
        System.out.println(userDetails.getUsername());
        // System.out.println(account.getUsername());

    /*    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean authenticated = authentication.isAuthenticated();*/
    }

    @Async // @Async 를 붙이면 특정 빈안에 있는 메서드를 호출할 때 별도의 쓰레드를 만들어서 비동기적으로 호출해준다.
    // Async 어노테이션만 붙이면 아무일도 일어나지 않는다.. Configuration(@ SpringBootApplication) 에 @EnableAsync 를 추가해야함
    public void asyncService() {
        SecurityLogger.log("Async Service");
        // -> principal 이 null 이라 NPE 발생, 즉 이 메서드를 실행하는 thread 에서 security context 가 공유가 안됨
        // 해결방법 -> SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL); 설정 ( 하위쓰레드에도 공유 )
        System.out.println("Async Service is called");
    }

}
