package jp.co.canon.rss.logmanager.vo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "error_log_download_setting", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ErrorLogDownloadSettingVo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Column(name = "site_id", nullable = false)
	private int siteId;

	@Column(name = "error_code", nullable = false)
	private String error_code;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "command", nullable = false)
	private String command;

	@Column(name = "before", nullable = false)
	private String before;

	@Column(name = "after", nullable = false)
	private String after;

	@ManyToOne
	@JoinColumn(name = "site_vo_list_fk")
	private SiteVo siteVoList;

}
