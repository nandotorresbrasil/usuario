package com.javanauta.usuario.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Inicializa o filtro customizado do JWT
        JwtRequestFilter jwtRequestFilter = new JwtRequestFilter(jwtUtil, userDetailsService);

        http
                .csrf(AbstractHttpConfigurer::disable) // Desativa proteção CSRF para APIs REST
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Endpoints Públicos (Acesso livre sem Token)
                        .requestMatchers("/usuario/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuario").permitAll()

                        // 2. Endpoints Protegidos (Exigem o cabeçalho Authorization Bearer)
                        .requestMatchers(HttpMethod.PUT, "/usuario/**").authenticated()   // Libera o PUT de endereço e telefone
                        .requestMatchers(HttpMethod.DELETE, "/usuario/**").authenticated() // Libera a deleção protegida
                        .requestMatchers(HttpMethod.GET, "/usuario/**").authenticated()    // Libera buscas protegidas

                        // 3. Qualquer outra rota não mapeada exige login
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Define a API como Stateless
                )
                // Injeta o filtro do JWT antes do filtro padrão de autenticação por usuário/senha
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Bean para criptografia de senhas
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // Gerenciador de autenticação do Spring
    }
}