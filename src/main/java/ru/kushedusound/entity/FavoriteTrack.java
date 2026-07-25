package ru.kushedusound.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "favourite_tracks", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "track_id"}))
@Getter
@Setter
@NoArgsConstructor
public class FavoriteTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt = LocalDateTime.now();

    public FavoriteTrack(User user, Track track) {
        this.user = user;
        this.track = track;
    }
}
