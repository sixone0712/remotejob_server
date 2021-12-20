package jp.co.canon.rss.logmanager.controller.model.job;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ReqLocalJob {
	private Integer siteId;
	private ArrayList<String> fileIndices;
}
