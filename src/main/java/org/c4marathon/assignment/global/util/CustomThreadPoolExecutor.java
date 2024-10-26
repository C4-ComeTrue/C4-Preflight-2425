package org.c4marathon.assignment.global.util;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomThreadPoolExecutor implements Executor {
	private int queueSize;
	private int threadCount;
	private ThreadPoolExecutor threadPoolExecutor;
	private RuntimeException exception;

	private void init() {
		this.queueSize = 100;
		this.threadCount = 8;
		this.threadPoolExecutor = new ThreadPoolExecutor(threadCount, threadCount, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>(queueSize), (r, executor) -> {
			try {
				executor.getQueue().put(r);
			} catch (InterruptedException e) {
				log.error(e.toString(), e);
				Thread.currentThread().interrupt();
			}
		});
	}

	@Override
	public void execute(Runnable command) {
		if (threadPoolExecutor != null && (threadPoolExecutor.isTerminated() || threadPoolExecutor.isTerminating())) {
			return;
		}

		init();

		threadPoolExecutor.execute(() -> {
			try {
				command.run();
			} catch (RuntimeException e) {
				log.error("처리 중 예외 발생", e);
				this.exception = e;
			}
		});
	}

	public void waitToEnd() {
		if (isInvalidState()) {
			return;
		}

		this.threadPoolExecutor.shutdown();

		while (true) {
			try {
				if (threadPoolExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
					break;
				}
			} catch (InterruptedException e) {
				log.error(e.toString(), e);
				Thread.currentThread().interrupt();
			}
		}

		threadPoolExecutor = null; // 임무를 다한 Executor 지우기

		if (exception != null) { // hold한 예외 던지기
			throw exception;
		}
	}

	private boolean isInvalidState() {
		return threadPoolExecutor == null || threadPoolExecutor.isTerminating() || threadPoolExecutor.isTerminated();
	}
}
