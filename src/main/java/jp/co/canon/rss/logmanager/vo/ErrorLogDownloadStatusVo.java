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
@Table(name = "error_log_download_status", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ErrorLogDownloadStatusVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "site_id", nullable = false)
    private int siteId;
    @Column(name = "rid", nullable = false)
    private String rid;

    @Column(name = "occurred_date", nullable = false)
    private String occurred_date;

    @Column(name = "equipment_name", nullable = false)
    private String equipment_name;

    @Column(name = "download_url")
    private String download_url;

    @Column(name = "saved_file_name")
    private String saved_file_name;

    @Column(name = "download_id")
    private String download_id;

    @Column(name = "error_code", nullable = false)
    private String error_code;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "command", nullable = false)
    private String command;

    @Column(name = "start_time", nullable = false)
    private String start;

    @Column(name = "end_time", nullable = false)
    private String end;

    @Column(name = "device", nullable = false)
    private String device;

    @Column(name = "process", nullable = false)
    private String process;

    @Column(name = "status")
    private String status;

    @Column(name = "error")
    private String error;

    @ManyToOne
    @JoinColumn(name = "site_vo_list_fk")
    private SiteVo siteVoList;
}
