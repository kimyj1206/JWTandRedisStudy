package project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티를  활성화하고 웹 보안 설정을 구성하는데 사용
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring()
                .requestMatchers("/static/**"));
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        // 비밀번호를 db에 저장하기 전 사용할 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()
                .requestMatchers("/login", "/signup", "/api/join", "/api/login", "/error", "/users", "/userInfo/**", "/api/**").permitAll()  // 해당 경로의 요청만 모든 사용자가 접근 가능
                .anyRequest().authenticated() // 그 외 요청은 모두 인증이 필요함

                .and()
                .formLogin()
                .loginPage("/login") // 로그인 페이지 경로 지정
                .defaultSuccessUrl("/users")

                .and()
                .csrf().disable()
                .httpBasic().disable()
                .cors().disable()
                .logout() // 로그아웃 설정
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true);

        return http.build();
    }
}
