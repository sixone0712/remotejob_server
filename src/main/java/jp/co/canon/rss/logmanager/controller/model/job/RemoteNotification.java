package jp.co.canon.rss.logmanager.controller.model.job;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class RemoteNotification {
	private ArrayList<Integer> recipients;
	private String subject;
	private String body;
	private Integer before;
}
