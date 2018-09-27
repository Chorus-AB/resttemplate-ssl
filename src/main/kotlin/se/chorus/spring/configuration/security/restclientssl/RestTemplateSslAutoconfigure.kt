package se.chorus.spring.configuration.security.restclientssl

import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.security.KeyStore
import javax.annotation.Resource

@Configuration
@ConditionalOnBean(RestTemplateBuilder::class)
@ConditionalOnProperty(prefix = "restclient.ssl", name = ["enabled"], havingValue = "true")
class RestTemplateSslAutoconfigure {

    @Resource lateinit var properties: RestBuilderSslProperties
    @Resource lateinit var loader: ResourceLoader

    @Bean
    fun clientHttpsRequestFactory(): ClientHttpRequestFactory {

        fun createKeystore(values: RestBuilderSslProperties.JavaKeystore): KeyStore? =
            KeyStore.getInstance(values.type)?.apply {
                load(loader.getResource(values.file).inputStream, values.password.toCharArray())
            }

        val sslContext = with(SSLContexts.custom()) {
            loadKeyMaterial(createKeystore(properties.keystore), properties.keystore.password.toCharArray()) { _, _ -> properties.keystore.alias }
                    .loadKeyMaterial(createKeystore(properties.truststore), properties.truststore.password.toCharArray())
                    .build()
        }
        return with(HttpClients.custom()) {
            setSSLSocketFactory(SSLConnectionSocketFactory(
                    sslContext,
                    properties.supportedprotocols,
                    properties.supportedciphersuites,
                    if (properties.cncheck) SSLConnectionSocketFactory.getDefaultHostnameVerifier() else NoopHostnameVerifier()
            ))
            HttpComponentsClientHttpRequestFactory(build())
        }
    }

    @Bean
    fun sslRestTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate =
        restTemplateBuilder.requestFactory {
            clientHttpsRequestFactory()
        }.build()

    @Bean
    @ConditionalOnProperty(prefix = "restclient.ssl", name = ["forall"], havingValue = "true")
    fun sslCustomizer(httpsRequestFactory: ClientHttpRequestFactory): RestTemplateCustomizer
            = RestTemplateCustomizer { restTemplate -> restTemplate.requestFactory = clientHttpsRequestFactory() }
}