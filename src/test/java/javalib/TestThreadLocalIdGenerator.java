package javalib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import org.danilopianini.concurrency.ThreadLocalIdGenerator;
import org.junit.Test;

/**
 * @author Danilo Pianini
 *
 */
public class TestThreadLocalIdGenerator {

	private static final int TESTS = 100000;
	private static final int THREADS = 100;
	
	/**
	 * 
	 */
	@Test
	public void test() {
		final ThreadLocalIdGenerator idgen = new ThreadLocalIdGenerator();
		final IntConsumer act = i -> idgen.genId();
		final Semaphore starter = new Semaphore(0);
		final CountDownLatch cdl = new CountDownLatch(THREADS);
		final Runnable run = () -> {
			starter.acquireUninterruptibly();
			IntStream.range(0, TESTS).forEach(act);
		 	assertEquals(ThreadLocalIdGenerator.class + " generates broken IDs!", TESTS, idgen.genId());
		 	cdl.countDown();
		};
		IntStream.range(0, THREADS).forEach(i -> new Thread(run).start());
		starter.release(THREADS);
		try {
			cdl.await();
		} catch (InterruptedException e) {
			fail();
		}
	}

}
