package se.chorus.spring.configuration.security.restclientssl

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("restclient.ssl")
class RestBuilderSslProperties {

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

    class JavaKeystore() {
        lateinit var file: String
        lateinit var password: String
        lateinit var type: String
        var alias: String? = null
    }
}