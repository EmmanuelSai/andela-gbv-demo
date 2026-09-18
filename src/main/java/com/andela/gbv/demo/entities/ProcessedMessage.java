package com.andela.gbv.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "processed_messages")
public class ProcessedMessage {
    @Id
    @Column(name = "message_sid", length = 64)
    private String messageSid;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt = Instant.now();
}
