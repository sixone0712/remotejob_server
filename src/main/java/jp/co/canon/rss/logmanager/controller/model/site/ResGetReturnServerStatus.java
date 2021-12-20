package jp.co.canon.rss.logmanager.controller.model.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema
public class ResGetReturnServerStatus {
	public String result;
	public String serverStatus;
}
