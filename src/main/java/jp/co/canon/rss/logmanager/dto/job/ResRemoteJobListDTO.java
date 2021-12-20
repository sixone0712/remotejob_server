package jp.co.canon.rss.logmanager.dto.job;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ResRemoteJobListDTO {
    private Integer jobId;
    private int siteId;
    private boolean stop;
    private String companyName;
    private String fabName;
    private String jobName;
    @Schema(description = "success, failure, notbuild, processing")
    private String collectStatus;
    @Schema(description = "success, failure, notbuild, processing")
    private String convertStatus;
    @Schema(description = "success, failure, notbuild, processing")
    private String errorSummaryStatus;
    @Schema(description = "success, failure, notbuild, processing")
    private String crasDataStatus;
    @Schema(description = "success, failure, notbuild, processing")
    private String mpaVersionStatus;
    @Schema(description = "success, failure, notbuild, processing")
    private String dbPurgeStatus;
    private String [] collectError;
    private String [] convertError;
    private String [] errorSummaryError;
    private String [] crasDataError;
    private String [] mpaVersionError;
    private String [] dbPurgeError;
}
