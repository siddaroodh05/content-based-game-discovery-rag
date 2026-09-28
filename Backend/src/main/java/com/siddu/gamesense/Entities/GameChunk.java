package com.siddu.gamesense.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.siddu.gamesense.enums.ChunkType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "game_chunks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameChunk {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonIgnore
    private Game game;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChunkType chunkType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1024)
    private float[] embedding;


}