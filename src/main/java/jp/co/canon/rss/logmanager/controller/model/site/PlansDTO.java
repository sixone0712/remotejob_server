package jp.co.canon.rss.logmanager.controller.model.site;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PlansDTO {
	private int planId;
	private String planName;
	private String planType;
	private String[] machineNames;
	private String[] targetNames;
	private String description;
	private String status;
}
