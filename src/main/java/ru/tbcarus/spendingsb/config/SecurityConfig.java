package ru.tbcarus.spendingsb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/images/**", "/test", "/users").permitAll()
                        .anyRequest().authenticated())
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll())
//                .formLogin(form -> form.permitAll())
                .logout((logout) -> logout.permitAll())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

//    // IN MEMORY AUTHENTICATION EXAMPLE
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
//        List<UserDetails> usersList = new ArrayList<>();
//        usersList.add(new User("buzz", encoder.encode("password"), Arrays.asList(Role.USER)));
//        usersList.add(new User("woody", encoder.encode("password"), Arrays.asList(Role.ADMIN)));
//        return new InMemoryUserDetailsManager(usersList);
//    }
}
