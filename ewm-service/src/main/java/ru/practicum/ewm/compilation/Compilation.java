package ru.practicum.ewm.compilation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.ewm.event.Event;

import java.util.List;

@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "Compilation.forMapping",
                attributeNodes = {
                        @NamedAttributeNode(value = "events", subgraph = "events-subgraph")
                },
                subgraphs = {
                        @NamedSubgraph(
                                name = "events-subgraph",
                                attributeNodes = {
                                        @NamedAttributeNode("category"),
                                        @NamedAttributeNode("initiator"),
                                        @NamedAttributeNode("location")
                                }
                        )
                }
        )
})
@Table(name = "compilations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "pinned", nullable = false)
    private Boolean pinned;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;
}
