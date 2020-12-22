package me.summerbell.springsecurity.config;

import me.summerbell.springsecurity.account.AccountService;
import me.summerbell.springsecurity.common.LoggingFIlter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE-50)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService;

    // AccessDecisionManager customize (default - AffirmativeAccessDecisionManage)
//    public AccessDecisionManager accessDecisionManager(){
//        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
//
//        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
//        handler.setRoleHierarchy(roleHierarchy);
//
//        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
//        webExpressionVoter.setExpressionHandler(handler);
//        List<AccessDecisionVoter<? extends Object>> voters = Arrays.asList(webExpressionVoter);
//        return new AffirmativeBased(voters);
//
//        /*
//        AccessDecisionManger 은 AccessDecisionVoter 를 사용하고
//        AccessDecisionVoter 는 ExpressionHandler 를 사용한다.
//        ExpressionHandler 에서 Role hierarchy 를 설정할 수 있다
//         */

//    }
    // 위 코드가 상당히 장황하다... accessDecisionManager 대신 expressionHandler 를 갈아낄수도 있다.
     public SecurityExpressionHandler expressionHandler(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);


        return handler;
    }

    // @Async 를 사용한 비동기 작업에서 SecurityContext 를 공유하기 위한 SecurityContextHolder 전략 수정 설정
    protected SecurityConfig() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    // 요청에 스프링 시큐리티 필터를 적용시키고 싶지 않을 때
    @Override
    public void configure(WebSecurity web) throws Exception {
         // 직접 ignore 할 요청 설정
         // web.ignoring().mvcMatchers("/somerequest");

         // static 자원에 대한 요청에 필터를 적용하고 싶지 않은 경우
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /*
    HttpSecurity 의 설정에 적용시킨다면, 결과는 같겠지만 filter 를 타게됨으로 불필요한 리소스가 소모된다. 그래서 시큐리티 필터를 탈 필요가 없는 경우라면,
    WebSecurity 의 설정을 통해 필터를 아예 적용시키지 않는게 좋다.
    단, 동적으로 처리하는 리소스는 HttpSecurity 에서 관리해주는게 좋은데 예를들어 루트 페이지의 경우 인증된 사용자와 인증이 안된 사용자가 모두 접근이 가능하더라도,
    인증된 사용자가 루트로 접근한다면, 필터를 거치며 SecurityContextHolder 에 들어가기 때문에 필터 처리를 하는게 좋다.
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/info", "/account/**", "/signup/**").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .mvcMatchers("/user").hasRole("USER")
                .anyRequest().authenticated()
                .expressionHandler(expressionHandler());
                //.accessDecisionManager(accessDecisionManager());

        http.formLogin()
                .loginPage("/login")
                .permitAll();


        http.httpBasic();

        http.sessionManagement()
                .maximumSessions(1);

        http.exceptionHandling()
                .accessDeniedPage("/access-denied");

        http.rememberMe()
                .userDetailsService(accountService)
                .key("remember-me");

        http.addFilterBefore(new LoggingFIlter(), WebAsyncManagerIntegrationFilter.class);

    }

    /* inMemory 유저정보를 설정하기.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("jaden").password("{noop}123").roles("USER")
                .and()
                .withUser("admin").password("{noop}admin").roles("ADMIN");
    }
     */


}

