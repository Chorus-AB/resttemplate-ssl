/*-
 * #%L
 * SSL autoconfiguration of RestTemplate
 * %%
 * Copyright (C) 2018 Chorus AB
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package se.chorus.spring.configuration.security.restclientssl

import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
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
open class RestTemplateSslAutoconfigure {

    @Resource lateinit var loader: ResourceLoader

    @Bean
    @ConditionalOnProperty(prefix = "restclient.ssl", name = ["enabled"], havingValue = "true")
    open fun clientHttpsRequestFactory(properties: RestBuilderSslProperties = properties()): ClientHttpRequestFactory {

        fun createKeystore(values: RestBuilderSslProperties.JavaKeystore): KeyStore? =
            KeyStore.getInstance(values.type)?.apply {
                load(loader.getResource(values.file!!).inputStream, values.password.toCharArray())
            }

        val sslContext = with(SSLContexts.custom()) {
            loadKeyMaterial(createKeystore(properties.keystore), properties.keystore.password.toCharArray()) { _, _ -> properties.keystore.alias }
                    .loadTrustMaterial(createKeystore(properties.truststore), TrustSelfSignedStrategy())
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
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @ConditionalOnProperty(prefix = "restclient.ssl", name = ["enabled"], havingValue = "true")
    open fun sslRestTemplate(restTemplateBuilder: RestTemplateBuilder, properties: RestBuilderSslProperties, httpsRequestFactory: ClientHttpRequestFactory): RestTemplate =
        restTemplateBuilder.build().apply { requestFactory = httpsRequestFactory }

    @Bean
    @ConditionalOnProperty(prefix = "restclient.ssl", name = ["forall"], havingValue = "true")
    @ConditionalOnBean(ClientHttpRequestFactory::class)
    open fun sslCustomizer(httpsRequestFactory: ClientHttpRequestFactory): RestTemplateCustomizer
            = RestTemplateCustomizer { restTemplate -> restTemplate.requestFactory = clientHttpsRequestFactory() }

    @Bean
    @ConfigurationProperties("restclient.ssl")
    open fun properties(): RestBuilderSslProperties = RestBuilderSslProperties()
}
