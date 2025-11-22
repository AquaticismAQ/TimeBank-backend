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

/**
 * Entity representing accumulated statistics and details for a student account.
 * Tracks points, credits, and request statistics for each student user.
 */
@Entity
@Table(name = "stu_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StuDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "accumulated_points", nullable = false)
    private int accumulatedPoints;

    @Column(name = "accumulated_credits", nullable = false)
    private int accumulatedCredits;

    @Column(name = "requests_made", nullable = false)
    private int requestsMade;

    @Column(name = "requests_approved", nullable = false)
    private int requestsApproved;

    @Column(name = "total_point_additions", nullable = false)
    private int totalPointAdditions;
}
