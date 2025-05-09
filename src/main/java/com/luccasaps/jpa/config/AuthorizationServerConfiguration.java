package com.luccasaps.jpa.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfiguration {

    @Order(1)
    @Bean
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Configurar para corresponder apenas a caminhos do servidor de autorização
        //http.securityMatcher("/oauth2/**",  "/login", "/connect/register");

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http); //⇐ method "applyDefaultSecurity()" is deprecated on version 3.4.4 of Spring boot

        //http.with(OAuth2AuthorizationServerConfigurer.authorizationServer(), Customizer.withDefaults()); // ⇐ solução do metodo deprecated

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        http.oauth2ResourceServer(
                oauth2Rs -> oauth2Rs.jwt(Customizer.withDefaults()));

        http.formLogin(configurer -> configurer.loginPage("/login"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public TokenSettings tokenSettings() {
        return TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                //access_token: utilizado nas requisições
                .accessTokenTimeToLive(Duration.ofMinutes(60))
                //refresh_token: utilizado para renovar o access_token
                .refreshTokenTimeToLive(Duration.ofMinutes(90))
                .build();
    }

    @Bean
    public ClientSettings clientSettings() {
        return ClientSettings.builder()
                .requireAuthorizationConsent(false)
                .build();
    }

    //JWK - JSON Web Key
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        RSAKey key = gerarChaveRSA();
        JWKSet jwkSet = new JWKSet(key);
        return new ImmutableJWKSet<>(jwkSet);
    }

    // Gerar par de chaves RSA
    private RSAKey gerarChaveRSA() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey chavePublica = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateCrtKey chavePrivada = (RSAPrivateCrtKey) keyPair.getPrivate();

        return new RSAKey.Builder(chavePublica)
                .privateKey(chavePrivada)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                //obter token independente do grant_type
                .tokenEndpoint("/oauth2/token")
                //obter informações do token
                .tokenIntrospectionEndpoint("/oauth2/introspect")
                //revogar token antes do tempo dele terminar
                .tokenRevocationEndpoint("/oauth2/revoke")
                //encaminha para o formulario de login
                .authorizationEndpoint("/oauth2/authorize")
                //informações do usuario OPEN ID CONNECT
                .oidcUserInfoEndpoint("/oauth2/userinfo")
                // obter a chave publica para verificar assinatura do token
                .jwkSetEndpoint("/oauth2/jwks")
                // logout
                .oidcLogoutEndpoint("/oauth2/logout")
                .build();
    }
}