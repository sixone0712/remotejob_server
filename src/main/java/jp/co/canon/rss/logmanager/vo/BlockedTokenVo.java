package jp.co.canon.rss.logmanager.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "blocked_token")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
public class BlockedTokenVo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(columnDefinition = "uuid", nullable = false, unique = true)
	private UUID uuid;

	@Column(nullable = false)
	private LocalDateTime expired;
}
