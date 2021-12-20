package jp.co.canon.rss.logmanager.controller.model.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema
public class ResGetReturnJobStatus {
	public String status;
	@Schema(description = "return : running, stopped, none")
	public String jobStatus;
}
