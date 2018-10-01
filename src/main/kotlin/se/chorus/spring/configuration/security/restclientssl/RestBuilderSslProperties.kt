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

import org.apache.http.ssl.PrivateKeyDetails
import org.springframework.boot.context.properties.NestedConfigurationProperty
import java.net.Socket

class RestBuilderSslProperties {

    /**
     * Turns injection of ssl-enabled httprequestfactory on or off
     */
    var enabled:Boolean = true
    var forall:Boolean = false
    var cncheck:Boolean = true

    @NestedConfigurationProperty
    var truststore: JavaKeystore = JavaKeystore().apply { type = "PKCS12" }

    @NestedConfigurationProperty
    var keystore: JavaKeystore = JavaKeystore().apply {
        type = "PKCS12"
        alias = "1"
    }

    var supportedprotocols: Array<String>? = arrayOf("TLSv1.2")
        set(value) {
            field = if(value?.count() == 0) null else value
        }
    var supportedciphersuites: Array<String>? = null
        set(value) {
            field = if(value?.count() == 0) null else value
        }

    class JavaKeystore {
        var file: String? = null
            set(value) {
                field = if (value?.trim() == "") null else value
            }
        var password: String? = null
        var type: String? = null
        var alias: String? = null
            set(value) {
                field = if (value?.trim() == "") null else value
            }

        fun enabled(): Boolean {
            return file != null && type != null
        }

        fun aliasStrategy(): ((aliases: Map<String, PrivateKeyDetails>, socket: Socket) -> String)? =
                    if(alias != null) { _,_ -> alias!!}
                    else null
    }
}
