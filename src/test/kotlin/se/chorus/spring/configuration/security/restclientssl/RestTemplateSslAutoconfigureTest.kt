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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate
import javax.annotation.Resource

@ExtendWith(SpringExtension::class)
internal class RestTemplateSslAutoconfigureTest {

    val withoutCustomization = arrayOf(
            "restclient.ssl.enabled=true",
            "restclient.ssl.cncheck=false",
            "restclient.ssl.truststore.file=classpath:client_truststore.jks",
            "restclient.ssl.truststore.password=changeit",
            "restclient.ssl.keystore.file=classpath:unittest.p12",
            "restclient.ssl.keystore.password=changeit",
            "restclient.ssl.keystore.type=PKCS12",
            "restclient.ssl.keystore.alias=unittest")

    val withCustomization = arrayOf(
            "restclient.ssl.enabled=true",
            "restclient.ssl.cncheck=false",
            "restclient.ssl.forall=true",
            "restclient.ssl.truststore.file=classpath:client_truststore.jks",
            "restclient.ssl.truststore.password=changeit",
            "restclient.ssl.keystore.file=classpath:unittest.p12",
            "restclient.ssl.keystore.password=changeit",
            "restclient.ssl.keystore.type=PKCS12",
            "restclient.ssl.keystore.alias=unittest")
    val contextRunner = ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RestclientSslApplication::class.java))

    @Test
    fun builder_should_not_customize() {

        contextRunner.withPropertyValues(*withoutCustomization).run {

            assertThat(it)
                    .hasBean("sslRestTemplate")
                    .doesNotHaveBean("sslCustomizer")
                    .hasBean("clientHttpsRequestFactory")
                    .hasBean("properties")
        }
    }

    @Test
    fun beans_exist_in_context() {

        contextRunner.withPropertyValues(*withCustomization).run {

            assertThat(it)
                    .hasBean("sslRestTemplate")
                    .hasBean("sslCustomizer")
                    .hasBean("clientHttpsRequestFactory")
                    .hasBean("properties")
        }
    }
}
