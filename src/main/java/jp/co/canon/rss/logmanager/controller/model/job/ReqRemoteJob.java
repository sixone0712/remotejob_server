package jp.co.canon.rss.logmanager.controller.model.job;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqRemoteJob extends ResRemoteJobDetail{
	private Integer siteId;
}
