package ru.kushedusound.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "playlist_tracks", uniqueConstraints = @UniqueConstraint(columnNames = {"playlist_id", "track_id"}))
@Getter
@Setter
@NoArgsConstructor
public class PlaylistTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Track track;

    @Column(name = "position", nullable = false)
    private int position;

    public PlaylistTrack(Playlist playlist, Track track, int position) {
        this.playlist = playlist;
        this.track = track;
        this.position = position;
    }
}