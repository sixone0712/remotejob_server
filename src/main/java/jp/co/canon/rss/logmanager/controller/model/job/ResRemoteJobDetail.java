package jp.co.canon.rss.logmanager.controller.model.job;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ResRemoteJobDetail {
	private ArrayList<Integer> planIds;
	private ArrayList<String> sendingTimes;
	private Boolean isErrorSummary;
	private Boolean isCrasData;
	private Boolean isMpaVersion;
	private RemoteNotification errorSummary;
	private RemoteNotification crasData;
	private RemoteNotification mpaVersion;
}
