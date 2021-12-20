package jp.co.canon.rss.logmanager.manager;

public class LocalJobStatus extends JobStatus{
    public LocalJobStatus(
            String jobId,
            String jobType,
            JobManager manager
    ){
        super(jobId, jobType, manager);
    }
}
