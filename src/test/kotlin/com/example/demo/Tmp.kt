package com.example.demo

import brave.Tracing
import brave.handler.MutableSpan
import brave.handler.SpanHandler
import brave.propagation.ThreadLocalCurrentTraceContext
import brave.propagation.TraceContext
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentLinkedDeque

class Tmp {
    private val spans = ConcurrentLinkedDeque<MutableSpan>()
    private val tracing = Tracing.newBuilder()
        .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder().build())
        .addSpanHandler(object : SpanHandler() {
            override fun end(context: TraceContext, span: MutableSpan, cause: Cause): Boolean {
                spans.add(span)
                return true
            }
        })
        .build()

    @Test
    fun hoge() {
        println(tracing.currentTraceContext().get())

        val span = tracing.tracer().nextSpan().name("encode").start()
        println(tracing.currentTraceContext().get())
        try {
            tracing.tracer().withSpanInScope(span).use { ws ->
                println(tracing.currentTraceContext().get())
            }
        } catch (e: RuntimeException) {
            span.error(e)
            throw e
        } catch (e: Error) {
            span.error(e)
            throw e
        } finally {
            span.finish()
        }

        println(tracing.currentTraceContext().get())
    }
}