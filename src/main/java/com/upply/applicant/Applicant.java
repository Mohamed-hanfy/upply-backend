package com.upply.applicant;

import com.upply.applicant.enums.ApplicantStatus;
import com.upply.job.Job;
import com.upply.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "applicants")
public class Applicant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "applicant_user_id")
    private User applicant;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime applyTime;

    String resume;

    @Enumerated(EnumType.STRING)
    ApplicantStatus status;

    double matchingRatio;

}
