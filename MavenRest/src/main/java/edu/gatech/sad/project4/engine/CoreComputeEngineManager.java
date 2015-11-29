package edu.gatech.sad.project4.engine;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.gatech.sad.project4.NewHibernateUtil;
import edu.gatech.sad.project4.entities.Processingstatustable;

public class CoreComputeEngineManager {
	private static final Log log = LogFactory.getLog(CoreComputeEngineManager.class);

	public static final int DEFAULT_QUEUE_SIZE = 256;
	private int queueSize = DEFAULT_QUEUE_SIZE;

	public static final int UNDEFINED = -1;
	private int discardingThreshold = UNDEFINED;

	public static final int DEFAULT_MAX_FLUSH_TIME = 10000;
	private int maxFlushTime = DEFAULT_MAX_FLUSH_TIME;

	public enum EngineManagerEventType { ENGINE_START, ENGINE_RESUME, ENGINE_STOP} 

	private BlockingQueue<EngineManagerEventType> blockingQueue;
	private EventWorker eventWorker = new EventWorker();
	private String name;
	private boolean started = false;

	public void start() {
		if (queueSize < 1) {
			log.error("Invalid queue size [" + queueSize + "]");
			return;
		}
		blockingQueue = new ArrayBlockingQueue<EngineManagerEventType>(queueSize);

		if (discardingThreshold == UNDEFINED)
			discardingThreshold = queueSize / 5;
		eventWorker.setDaemon(true);
		eventWorker.setName("CoreComputeEngine-EventWorker-" + eventWorker.getId());
		// kickstart the process by triggering an ENGINE_STOP
		// this causes the system to check the DB for any currently
		// existing pending execution records and retarting them
		// ahead of any new processing.
		try {
			blockingQueue.put(EngineManagerEventType.ENGINE_RESUME);
		} catch (Exception e) {
			// since we just started, it shouldn't be possible to be blocked
			// so we'll just ignore it.
		}
		// make sure this instance is marked as "started" before staring the
		// worker Thread
		started = true;
		eventWorker.start();
	}

	public void stop() {
		if (!isStarted())
			return;

		// interrupt the worker thread so that it can terminate. Note that the
		// interruption can be consumed
		// by sub-appenders
		eventWorker.interrupt();
		try {
			eventWorker.join(maxFlushTime);

			// check to see if the thread ended and if not add a warning message
			if (eventWorker.isAlive()) {
				log.warn("Max queue flush timeout (" + maxFlushTime
						+ " ms) exceeded. Approximately "
						+ blockingQueue.size()
						+ " queued events were possibly discarded.");
			} else {
				log.info("Queue flush finished successfully within timeout.");
			}

		} catch (InterruptedException e) {
			log.error("Failed to join worker thread. " + blockingQueue.size()
					+ " queued events may be discarded.", e);
		}
	}

	public boolean isStarted() {
		return started;
	}

	/**
	 * Adds an event to the queue for the worker to consume.  If there is room in the queue,
	 * accepts the event and returns immediately.  Otherwise, blocks until vents are consumed.
	 * 
	 * @param eventObject
	 */
	public void triggerEngine() {
		try {
			blockingQueue.put(EngineManagerEventType.ENGINE_START);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Event Worker class.  Waits for events in the queue, the nreads and dispatches to
	 * the CoreComputeEngine.
	 * 
	 * IDLE: The EngineWorker
	 *
	 */
	class EventWorker extends Thread {
		private EngineWorker engineWorker = null;

		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			CoreComputeEngineManager parent = CoreComputeEngineManager.this;

			// loop while the parent is started
			while (parent.isStarted()) {
				try {
					EngineManagerEventType eventType = parent.blockingQueue.take();
					switch (eventType) {
					case ENGINE_START:
						handleEngineStart();
						break;
					case ENGINE_STOP:
						handleEngineStop();
						break;
					case ENGINE_RESUME:
						handleEngineResume();
						break;
					default:
						log.error("Unknown EngineManagerEventType:" + eventType);
					}
				} catch (InterruptedException ie) {
					break;
				}
			}

			log.info("Worker thread will flush remaining events before exiting. ");

			for (Object e : parent.blockingQueue) {
				// aai.appendLoopOnAppenders(e);
				parent.blockingQueue.remove(e);
			}

			// aai.detachAndStopAllAppenders();
		}

		private void handleEngineStart() {
			log.debug("handleEngineStart entry");
			// check DB for a "pending" record
			Session s = NewHibernateUtil.getSessionFactory().getCurrentSession();
			Transaction t = s.beginTransaction();
			// look for a pending record
			List<Processingstatustable> psList = s.createQuery("SELECT ps FROM Processingstatustable ps WHERE executionStartTime IS NULL AND completed = FALSE").list();

			// sanity check
			if (psList.size() > 1) {
				log.error("Processingstatustable in an inconsistent state, more than one record returned");
				System.exit(1);
			}
			// if none, then create a record and start the engine
			Processingstatustable ps = null;
			if (psList.isEmpty()) {
				log.debug("no pending records, creating one");
				ps = new Processingstatustable();
				ps.setRegisteredTime(new Date());
				Integer newId = (Integer)s.save(ps);
				log.debug("save returned newId:" + newId);
				s.flush();
				log.debug("saved new ps record with id " + ps.getId());
				if (ps.getId() == 0) {
					log.error("processingstatustable id was 0 after save");
					System.exit(1);
				}
			} else if (psList.size() == 1) {
				// have a record, so check if engine is running
				log.debug("pending record already exists");
				ps = psList.get(0);
			}

			t.commit();

			// if engine not running, start it
			if (engineWorker == null) {
				log.debug("engine not running, so starting one with newly created record");
				engineWorker = new EngineWorker();
				engineWorker.setDaemon(true);
				engineWorker.setName("CoreComputeEngine-EngineWorker-" + engineWorker.getId());
				engineWorker.setProcessingId(ps.getId());
				engineWorker.start();
			} else {
				log.debug("engine already running, nothing to do at this time");
			}

		}

		private void handleEngineStop() {
			// clear out old worker thread
			engineWorker = null;
			// check DB for a "pending" record
			Session s = NewHibernateUtil.getSessionFactory().getCurrentSession();
			Transaction t = s.beginTransaction();
			// look for a pending record
			List<Processingstatustable> psList = s.createQuery("SELECT ps FROM Processingstatustable ps WHERE executionStartTime IS NULL AND completed = FALSE").list();
			t.commit();

			//sanity check
			if (psList.size() > 1) {
				log.error("Processingstatustable in an inconsistent state, more than one record returned");
				System.exit(1);
			}

			if (psList.isEmpty()) {
				// if none, then just return, nothing to do
				return;
			}

			if (psList.size() == 1) {
				// we have a pending record, so start the engine 
				Processingstatustable ps = psList.get(0);
				engineWorker = new EngineWorker();
				engineWorker.setDaemon(true);
				engineWorker.setName("CoreComputeEngine-EngineWorker-" + engineWorker.getId());
				engineWorker.setProcessingId(ps.getId());
				engineWorker.start();
			}
		}

		/**
		 * Used to try to resume an engine run that didn't complete.
		 * Only used when starting the system.  Looks for a Processingstatustable
		 * record that has a startTime but is not marked as complete.
		 */
		private void handleEngineResume() {
			log.debug("handleEngineResume entry");
			Session s = NewHibernateUtil.getSessionFactory().getCurrentSession();
			Transaction t = s.beginTransaction();
			// look for an old record (started, but not completed)
			List<Processingstatustable> psList = s.createQuery("SELECT ps FROM Processingstatustable ps WHERE executionStartTime IS NOT NULL AND completed = FALSE").list();
			t.commit();
			if (psList.size() > 1) {
				log.error("error, too many processingstatustable records in partial run status (should be only 1");
				System.exit(1);
			}
			if (psList.isEmpty()) {
				// no old records to restart, so just return
				log.debug("no old records to resume");
				return;
			}
			if (psList.size() == 1) {
				log.debug("old record found, restarting the engine for this record");
				Processingstatustable ps = psList.get(0);
				engineWorker = new EngineWorker();
				engineWorker.setDaemon(true);
				engineWorker.setName("CoreComputeEngine-EngineWorker-" + engineWorker.getId());
				engineWorker.setProcessingId(ps.getId());
				engineWorker.start();
				return;
			}
		}
	}

	/**
	 * Engine Worker class.  Actually runs the engine on another thread so the Event
	 * worker can coalease events and handle DB tasks.
	 *
	 */
	class EngineWorker extends Thread {
		private int processingId;

		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			log.debug("running EngineWorker for processingId:" + processingId);
			CoreComputeEngine cce = new CoreComputeEngine(processingId);
			cce.solve();

			log.debug("CoreComputeEngine done, sending ENGINE_STOP event to queue");
			CoreComputeEngineManager parent = CoreComputeEngineManager.this;			
			try {
				parent.blockingQueue.put(EngineManagerEventType.ENGINE_STOP);
			} catch (InterruptedException ie) {
				
			}
			log.debug("ending EngineWorker for processingId:" + processingId);
		}

		public int getProcessingId() {
			return processingId;
		}

		public void setProcessingId(int processingId) {
			this.processingId = processingId;
		}
	}

}
