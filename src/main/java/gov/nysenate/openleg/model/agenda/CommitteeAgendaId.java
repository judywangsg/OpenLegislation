package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;
import java.util.Objects;

/**
 * Identifies a specific committee within an agenda.
 */
public class CommitteeAgendaId implements Serializable
{
    private static final long serialVersionUID = 6484855429442267908L;

    private AgendaId agendaId;
    private CommitteeId committeeId;

    /** --- Constructors --- */

    public CommitteeAgendaId(AgendaId agendaId, CommitteeId committeeId) {
        this.agendaId = agendaId;
        this.committeeId = committeeId;
    }

    @Override
    public String toString() {
        return "Agenda " + agendaId + " Committee " + committeeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final CommitteeAgendaId other = (CommitteeAgendaId) obj;
        return Objects.equals(this.agendaId, other.agendaId) && Objects.equals(this.committeeId, other.committeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agendaId, committeeId);
    }

    /** --- Basic Getters/Setters --- */

    public AgendaId getAgendaId() {
        return agendaId;
    }

    public CommitteeId getCommitteeId() {
        return committeeId;
    }
}
