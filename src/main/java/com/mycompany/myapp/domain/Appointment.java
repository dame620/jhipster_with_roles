package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Appointment.
 */
@Entity
@Table(name = "appointment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reason")
    private String reason;

    @Column(name = "date")
    private Instant date;

    @Column(name = "state")
    private Boolean state;

    @Column(name = "reportreason")
    private String reportreason;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "bank" }, allowSetters = true)
    private Adviser adviser;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "company" }, allowSetters = true)
    private Manager manager;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment id(Long id) {
        this.id = id;
        return this;
    }

    public String getReason() {
        return this.reason;
    }

    public Appointment reason(String reason) {
        this.reason = reason;
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getDate() {
        return this.date;
    }

    public Appointment date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Boolean getState() {
        return this.state;
    }

    public Appointment state(Boolean state) {
        this.state = state;
        return this;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getReportreason() {
        return this.reportreason;
    }

    public Appointment reportreason(String reportreason) {
        this.reportreason = reportreason;
        return this;
    }

    public void setReportreason(String reportreason) {
        this.reportreason = reportreason;
    }

    public Adviser getAdviser() {
        return this.adviser;
    }

    public Appointment adviser(Adviser adviser) {
        this.setAdviser(adviser);
        return this;
    }

    public void setAdviser(Adviser adviser) {
        this.adviser = adviser;
    }

    public Manager getManager() {
        return this.manager;
    }

    public Appointment manager(Manager manager) {
        this.setManager(manager);
        return this;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appointment)) {
            return false;
        }
        return id != null && id.equals(((Appointment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Appointment{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", date='" + getDate() + "'" +
            ", state='" + getState() + "'" +
            ", reportreason='" + getReportreason() + "'" +
            "}";
    }
}
