package com.vagsoft.bookstore.configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.vagsoft.bookstore.services.JwtService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/** Configuration class for JWT settings */
@Configuration
@Setter
@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private RSAPrivateKey privateKey;

    private RSAPublicKey publicKey;

    private Duration ttl;

    /**
     * Bean for configuring the JWT encoder This bean uses the public and private
     * keys to create a JWT encoder that can be used to generate JWT tokens.
     *
     * @return JwtEncoder instance
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        final var jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();

        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
    }

    /**
     * Bean for configuring the JWT decoder This bean uses the public key to create
     * a JWT decoder that can be used to validate and decode JWT tokens.
     *
     * @return JwtDecoder instance
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    /**
     * Bean for configuring the JWT service This bean uses the application name and
     * JWT encoder to create a JWT service that can be used to generate and validate
     * JWT tokens.
     *
     * @param appName
     *            the name of the application
     * @param jwtEncoder
     *            the JWT encoder
     * @return JwtService instance
     */
    @Bean
    public JwtService jwtService(@Value("${spring.application.name}") final String appName, final JwtEncoder jwtEncoder,
            final JwtDecoder jwtDecoder) {

        return new JwtService(appName, ttl, jwtEncoder, jwtDecoder);
    }
}
