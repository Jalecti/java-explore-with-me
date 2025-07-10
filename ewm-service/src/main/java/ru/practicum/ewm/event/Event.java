package ru.practicum.ewm.event;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;

@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "Event.forMapping",
                attributeNodes = {
                        @NamedAttributeNode("category"),
                        @NamedAttributeNode("initiator")
                }
        )
})
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;

    @NotBlank
    @Column(name = "description", nullable = false, length = 7000)
    private String description;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @NotNull
    @Embedded
    private Location location;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EventState state;

    @NotNull
    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit = 0;

    @NotNull
    @Column(name = "confirmed_requests", nullable = false)
    private Long confirmedRequests = 0L;

    @NotNull
    @Column(name = "views", nullable = false)
    private Long views = 0L;

    @NotNull
    @Column(name = "is_paid", nullable = false)
    private Boolean paid;

    @NotNull
    @Column(name = "is_request_moderation", nullable = false)
    private Boolean requestModeration = true;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

}
