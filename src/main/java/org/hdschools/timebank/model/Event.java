package org.hdschools.timebank.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "init_stu_id")
    private String initStuId;

    @Column(name = "init_sta_id")
    private String initStaId;

    @Column(name = "recv_stu_id")
    private String recvStuId;

    @Column(name = "recv_sta_id")
    private String recvStaId;

    @Column(name = "point_diff", nullable = false)
    private int pointDiff;

    @Column(name = "credit_diff", nullable = false)
    private int creditDiff;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "content_html")
    private String contentHtml;
}
