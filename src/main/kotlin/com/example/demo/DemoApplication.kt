package com.example.demo

import brave.sampler.Sampler
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Hooks
import zipkin2.Span
import zipkin2.reporter.Reporter

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    // To make it easy to reproduce, we will make it work with one thread.
    System.setProperty("reactor.netty.ioWorkerCount", "1")

    Hooks.enableAutomaticContextPropagation()
    runApplication<DemoApplication>(*args)
}

@RestController
class DemoController {
    @GetMapping("/hello")
    suspend fun hello(): String {
        log.info("MDC=${MDC.getCopyOfContextMap()}")
        return "Hello"
    }
}

@Configuration(proxyBeanMethods = false)
class DemoConfiguration {
    @Bean
    fun braveSpanReporter(): Reporter<Span> {
        return Reporter {
            log.info("$it")
        }
    }

    @Bean
    fun braveSampler(): Sampler {
        return Sampler.ALWAYS_SAMPLE
    }
}

private val log = LoggerFactory.getLogger("logger")
