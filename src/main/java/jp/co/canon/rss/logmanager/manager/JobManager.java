package jp.co.canon.rss.logmanager.manager;

import jp.co.canon.rss.logmanager.repository.LocalJobFileIdVoRepository;
import jp.co.canon.rss.logmanager.repository.LocalJobRepository;
import jp.co.canon.rss.logmanager.repository.RemoteJobRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class JobManager {
    private Thread parentThread;
    private Manager manager;

    @Autowired
    @Getter
    private RemoteJobRepository remoteJobRepository;

    @Autowired
    @Getter
    private LocalJobRepository localJobRepository;

    @Autowired
    @Getter
    private LocalJobFileIdVoRepository localJobFileIdVoRepository;

    @Value("${file.upload-dir}")
    @Getter
    private String uploadPath;

    @Value("${manager.log-collect-before}")
    @Getter
    private int logCollectBefore;

    public static final String REMOTE_TYPE = "remote";
    public static final String LOCAL_TYPE = "local";

    private JobManager instance;

//    @PostConstruct
    private void _init() {
        this.instance = this;
        parentThread = new Thread(parentRunner);
        parentThread.start();
    }

    private Runnable parentRunner = ()->{
        log.info("StatusManager root-thread start");

        int i = 0;
        while(true) {
            if(manager ==null || !manager.isAlive()) {
                manager = new Manager(i++);
                manager.setName("JobStatusManager-"+i);
                manager.start();
            }
            try {
                Thread.sleep(60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    public String requestRemoteStatus(String id) throws JobStatusException {
        if(manager!=null && manager.isAlive()) {
            return manager.requestRemoteStatusMonitor(id);
        }
        return null;
    }

    public String requestLocalStatus(String id) throws JobStatusException {
        if(manager!=null && manager.isAlive()) {
            return manager.requestLocalStatusMonitor(id);
        }
        return null;
    }

    public class StatusProc extends Thread {
        String  jobId;

        @Override
        public void run() {
            int i = 0;

            try {
                while (true) {
                    sleep(1000);
                    i++;
                    log.info("StatusProc run count : " + i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Manager extends Thread {

        private final int managerId;
        private AtomicLong key = new AtomicLong(1);;
        private Map<String, JobStatus> jobList = new HashMap<>();

        private Manager(int id) {
            this.managerId = id;
        }

        @Override
        public void run() {
            int i = 0;

            try {
                while (true) {
                    sleep(1000);

                    List<String> terminates = getTerminates();

                    shutdown(terminates);

                    //timeoutSelf();

                    i++;
                    //log.info("Job Manager run count : " + i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private List<String> getTerminates() {
            List<String> terminates = new ArrayList<>();
            jobList.forEach((jobId, job)->{
                if(!job.isAlive() && job.isDead()) {
                    log.info(jobId+" search task was terminated");
                    try {
                        job.join();
                    } catch (InterruptedException e) {
                        log.info("interrupt occurs when thread join");
                        e.printStackTrace();
                    }
                    terminates.add(jobId);
                }
            });
            return terminates;
        }

        private void shutdown(List<String> target) {
            for(String jobId: target) {
                jobList.remove(jobId);
                log.info("removed "+jobId+" in memory");
            }
        }

        private void addAndStartJob(JobStatus job) {
            jobList.put(job.getJobId(), job);
            job.start();
            log.info("status "+job.getJobId()+" start");
        }

        private String requestRemoteStatusMonitor(String id) throws JobStatusException{
            JobStatus job;

            job = new JobStatus.Builder()
                    .jobStatusType(REMOTE_TYPE)
                    .manager(instance)
                    .build(id);

            if (job == null) {
                log.info("failed to create status job");
                throw new JobStatusException(JobStatusException.Error.invalidParam);
            }

            if(jobList.containsKey(job.getJobId()) == false){
                addAndStartJob(job);
            }

            return job.getJobId();
        }

        private String requestLocalStatusMonitor(String id) throws JobStatusException{
            JobStatus job;
            job = new JobStatus.Builder()
                    .jobStatusType(LOCAL_TYPE)
                    .manager(instance)
                    .build(id);

            if (job == null) {
                log.info("failed to create status job");
                throw new JobStatusException(JobStatusException.Error.invalidParam);
            }

            if(jobList.containsKey(job.getJobId()) == false){
                addAndStartJob(job);
            }
            return job.getJobId();
        }

    }
}
