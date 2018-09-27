package se.chorus.spring.configuration.security.restclientssl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/*
    Support tests of context
*/
@SpringBootApplication
class RestclientSslApplication

fun main(args: Array<String>) {
    runApplication<RestclientSslApplication>(*args)
}
