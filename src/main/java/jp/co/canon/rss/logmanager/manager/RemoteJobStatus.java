package jp.co.canon.rss.logmanager.manager;

public class RemoteJobStatus extends JobStatus{
    public RemoteJobStatus(
            String jobId,
            String jobType,
            JobManager manager
    ){
        super(jobId, jobType, manager);
    }
}
