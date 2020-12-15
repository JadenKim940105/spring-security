package me.summerbell.springsecurity.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {
    // UserDetailService 는 DAO 를 통해 DB에 있는 정보를 가지고 authentication 을 할 떄 사용하는 인터페이스
    /*
        username 을 받아와 DB 에서 해당 유저 정보를 UserDetails 타입으로 리턴
     */

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;

    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null ){
            throw new UsernameNotFoundException(username);
        }

        return User.builder()                       // 받아온 데이터를 UserDetails 타입으로 쉽게 변경하기 위해 User 를 사용할 수 있다.
                .username(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public Account createNew(Account account) {
        account.encodePassword(passwordEncoder);
        return accountRepository.save(account);
    }
}
