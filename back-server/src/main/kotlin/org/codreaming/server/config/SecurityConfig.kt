package org.codreaming.server.config

import org.codreaming.server.security.CustomUserDetailsService
import org.codreaming.server.security.RestAuthenticationEntryPoint
import org.codreaming.server.security.TokenAuthenticationFilter
import org.codreaming.server.security.oauth2.CustomOAuth2UserService
import org.codreaming.server.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository
import org.codreaming.server.security.oauth2.OAuth2AuthenticationFailureHandler
import org.codreaming.server.security.oauth2.OAuth2AuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
class SecurityConfig(
        @Autowired val customUserDetailsService: CustomUserDetailsService,
        @Autowired val customOAuth2UserService: CustomOAuth2UserService,
        @Autowired val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
        @Autowired val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
        @Autowired val httpCookieOAuth2AuthorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository,
        @Autowired val tokenAuthenticationFilter: TokenAuthenticationFilter
) : WebSecurityConfigurerAdapter() {

//    /*
//      By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
//      the authorization request. But, since our service is stateless, we can't save it in
//      the session. We'll save the request in a Base64 encoded cookie instead.
//    */
//    @Bean
//    fun cookieAuthorizationRequestRepository(): HttpCookieOAuth2AuthorizationRequestRepository {
//        return HttpCookieOAuth2AuthorizationRequestRepository()
//    }

    public override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder
                .userDetailsService<UserDetailsService>(customUserDetailsService)
                .passwordEncoder(passwordEncoder())
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(http: HttpSecurity) {
        http
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(RestAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers("/",
                        "/error",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
                .permitAll()
                .antMatchers("/auth/**", "/oauth2/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)

        // Add our custom Token based authentication filter
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

//    override fun configure(http: HttpSecurity) {
//        http
//                .antMatcher("/**")
//                .authorizeRequests()
//                .antMatchers("/", "/login**", "/webjars/**", "/error**")
//                .permitAll()
//                .anyRequest()
//                .authenticated()
//                .and().logout().logoutSuccessUrl("/").permitAll()
//                .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//    }
}