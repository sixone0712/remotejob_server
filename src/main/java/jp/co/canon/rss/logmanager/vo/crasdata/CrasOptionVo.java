package jp.co.canon.rss.logmanager.vo.crasdata;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "cras_option", schema = "cras")
@Getter
@Setter
public class CrasOptionVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "cras", nullable = false)
    private String cras;
    @Column(name = "option", nullable = false)
    private String option;
    @Column(name = "value", nullable = false)
    private String value;
}
