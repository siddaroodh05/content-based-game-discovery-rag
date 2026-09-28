package com.siddu.gamesense.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_asin", nullable = false, unique = true)
    private String parentAsin;

    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private GameMetadata metadata;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GameChunk> chunks = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    private List<UserGame> userGames = new ArrayList<>();
}