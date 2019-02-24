package org.codreaming.server

import org.codreaming.server.config.AppProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(AppProperties::class)
@SpringBootApplication
class CodreamingServerApplication

fun main(args: Array<String>) {
    runApplication<CodreamingServerApplication>(*args)
}


inline fun <reified T> T.logger(): Lazy<Logger> {
    return lazy {
        if (T::class.isCompanion) {
            LoggerFactory.getLogger(T::class.java.enclosingClass)
        }
        LoggerFactory.getLogger(T::class.java)
    }
}