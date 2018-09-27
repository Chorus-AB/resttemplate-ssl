package se.chorus.spring.configuration.security.restclientssl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.annotation.Resource

@SpringBootTest(
        properties = [
            "restclient.ssl.enabled=true",
            "restclient.ssl.cncheck=false",
            "restclient.ssl.forall=true",
            "restclient.ssl.truststore.file=classpath:client_truststore.jks",
            "restclient.ssl.truststore.password=changeit",
            "restclient.ssl.keystore.file=classpath:unittest.p12",
            "restclient.ssl.keystore.password=changeit",
            "restclient.ssl.keystore.type=PKCS12",
            "restclient.ssl.keystore.alias=unittest"
        ]
)
@ExtendWith(SpringExtension::class)
internal class RestTemplateSslAutoconfigureAllTest {

    @Resource private lateinit var restTemplateBuilder: RestTemplateBuilder
    @Resource private lateinit var clientHttpsRequestFactory: HttpComponentsClientHttpRequestFactory

    @Test
    fun builder_should_customize() {
        assertThat(restTemplateBuilder.build().requestFactory).isEqualTo(clientHttpsRequestFactory)
    }
}