package gov.nysenate.openleg.model.entity;

import java.io.Serializable;
import java.util.Objects;

public class MemberId implements Serializable
{
    private static final long serialVersionUID = -3479872987089824973L;

    /** Id generated by the persistence layer which can uniquely identify a person within a
     *  specific legislative chamber. */
    private int id;

    /** Session year that this member is being referenced. */
    private int sessionYear;

    /** Short name that the source data uses. There may be multiple short names for a given member. */
    private String lbdcShortName;

    /** --- Constructors --- */

    public MemberId (int id, int sessionYear, String lbdcShortName) {
        this.id = id;
        this.sessionYear = sessionYear;
        this.lbdcShortName = lbdcShortName;
    }

    /** --- Overrides --- */

    @Override
    public String toString () {
        return "member #" + id + " lbdc (" + lbdcShortName + ") session (" + sessionYear + ")";
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final MemberId other = (MemberId) obj;
        return Objects.equals(this.id, other.id) &&
               Objects.equals(this.sessionYear, other.sessionYear) &&
               Objects.equals(this.lbdcShortName, other.lbdcShortName);
    }

    @Override
    public int hashCode () {
        return Objects.hash(id, sessionYear, lbdcShortName);
    }

    /** --- Basic Getters/Setters --- */

    public int getId () {
        return id;
    }

    public int getSessionYear () {
        return sessionYear;
    }

    public String getLbdcShortName () {
        return lbdcShortName;
    }
}