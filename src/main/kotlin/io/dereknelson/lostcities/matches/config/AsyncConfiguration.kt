package io.dereknelson.lostcities.matches.config

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Future

@Configuration
@EnableAsync
@EnableScheduling
class AsyncConfiguration(private val taskExecutionProperties: TaskExecutionProperties) : AsyncConfigurer {
    private val log = LoggerFactory.getLogger(AsyncConfiguration::class.java)

    @Bean("taskExecutor")
    override fun getAsyncExecutor(): Executor {
        log.debug("Creating Async Task Executor")
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = taskExecutionProperties.pool.coreSize
        executor.maxPoolSize = taskExecutionProperties.pool.maxSize
        executor.setQueueCapacity(taskExecutionProperties.pool.queueCapacity)
        executor.setThreadNamePrefix(taskExecutionProperties.threadNamePrefix)
        return ExceptionHandlingAsyncTaskExecutor(executor)
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return SimpleAsyncUncaughtExceptionHandler()
    }
}

internal class ExceptionHandlingAsyncTaskExecutor(private val executor: AsyncTaskExecutor) :
    AsyncTaskExecutor,
    InitializingBean,
    DisposableBean {
    private val log = LoggerFactory.getLogger(ExceptionHandlingAsyncTaskExecutor::class.java)
    override fun execute(task: Runnable) {
        executor.execute(task)
    }

    override fun execute(task: Runnable, startTimeout: Long) {
        executor.execute(createWrappedRunnable(task), startTimeout)
    }

    private fun <T> createCallable(task: Callable<T>): Callable<T> {
        return Callable {
            try {
                task.call()
            } catch (e: java.lang.Exception) {
                handle(e)
                throw e
            }
        }
    }

    private fun createWrappedRunnable(task: Runnable): Runnable {
        return Runnable {
            try {
                task.run()
            } catch (e: java.lang.Exception) {
                handle(e)
            }
        }
    }

    protected fun handle(e: Exception?) {
        log.error("Caught async exception", e)
    }

    override fun submit(task: Runnable): Future<*> {
        return executor.submit(createWrappedRunnable(task))
    }

    override fun <T> submit(task: Callable<T>): Future<T> {
        return executor.submit(createCallable(task))
    }

    @Throws(Exception::class)
    override fun destroy() {
        if (executor is DisposableBean) {
            val bean = executor as DisposableBean
            bean.destroy()
        }
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        if (executor is InitializingBean) {
            val bean = executor as InitializingBean
            bean.afterPropertiesSet()
        }
    }
}
