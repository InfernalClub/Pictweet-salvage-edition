package cl.ucn.disc.dsm.pictwin.model;

import io.ebean.annotation.NotNull;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import lombok.*;

import java.time.Instant;

/** The PicTwin. */
@Getter
@ToString(callSuper = true)
@AllArgsConstructor
@Builder
@Entity
public class PicTwin {

    /** The PicTwin. */
    @NotNull private Instant expiration;

    /** The expired. */
    @Builder.Default @NotNull private Boolean expired = Boolean.FALSE;

    /** The reported. */
    @Builder.Default @NotNull private Boolean reported = Boolean.FALSE;

    /** The persona relationship. */
    @ManyToOne(optional = false)
    private Persona persona;

    /** The Pic relationship */
    @ToString.Exclude
    @ManyToOne(optional = false)
    private Pic pic;

    /** Tje Pic relationship. */
    @ToString.Exclude
    @ManyToOne(optional = false)
    private Pic twin;


}
