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

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.annotation.Resource
import org.assertj.core.api.Assertions.*

@SpringBootTest
@ExtendWith(SpringExtension::class)
internal class RestBuilderSslPropertiesTest {

    @Resource private lateinit var properties: RestBuilderSslProperties

    @Test
    fun verifyPropertiesSet() {
        assertThat(properties).isNotNull
                .hasFieldOrPropertyWithValue("enabled", false)
                .hasFieldOrPropertyWithValue("cncheck", true)

        with(properties.truststore) {
            assertThat(this)
                    .hasFieldOrPropertyWithValue("file", "some/file")
                    .hasFieldOrPropertyWithValue("password", "random")
        }
        with(properties.keystore) {
            assertThat(this)
                    .hasFieldOrPropertyWithValue("file", "other/file")
                    .hasFieldOrPropertyWithValue("password", "random2")
                    .hasFieldOrPropertyWithValue("alias", "2")
        }
    }

    @Test
    fun truststore_should_have_default_values() {
        assertThat(properties.truststore).hasFieldOrPropertyWithValue("type", "PKCS12")
    }

    @Test
    fun keystore_should_override_default_values() {
        assertThat(properties.keystore).hasFieldOrPropertyWithValue("type", "ASDF")
    }

    @Test
    fun store_alias_can_be_null() {
        assertThat(properties.truststore).hasFieldOrPropertyWithValue("alias", null)
    }

    @Test
    fun comma_separated_values_handled_as_lists() {
        assertThat(properties.supportedprotocols)
                .contains("TLSv1.2", "TLSv1")
                .hasSize(2)
    }

    @Test
    fun empty_lists_are_null() {
        assertThat(properties)
                .hasFieldOrPropertyWithValue("supportedciphersuites", null)
    }
}
