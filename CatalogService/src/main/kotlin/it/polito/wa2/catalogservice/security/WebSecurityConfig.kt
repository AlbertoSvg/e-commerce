package it.polito.wa2.catalogservice.security

import it.polito.wa2.catalogservice.services.implementations.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    @Autowired
    val passwordEncoder: PasswordEncoder,
    @Autowired
    val userDetailsService: UserDetailsServiceImpl,
    @Autowired
    val authenticationEntryPoint: AuthEntryPoint
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun authTokenFilter(): JwtAuthenticationTokenFilter = JwtAuthenticationTokenFilter()

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(web: WebSecurity) {
        super.configure(web)
    }

    override fun configure(http: HttpSecurity) {
        http.cors()
            .and()
            .csrf()
            .disable()
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/auth/**")
            .permitAll()
            .antMatchers("/wallet/**")
            .authenticated()
            .anyRequest()
            .authenticated()

        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }
}
