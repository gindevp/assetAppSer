package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.app.domain.enumeration.ReturnRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Yêu cầu thu hồi
 */
@Entity
@Table(name = "return_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "code", length = 20, nullable = false, unique = true)
    private String code;

    @NotNull
    @Column(name = "request_date", nullable = false)
    private Instant requestDate;

    @Size(max = 2000)
    @Column(name = "note", length = 2000)
    private String note;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReturnRequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "department" }, allowSetters = true)
    private Employee requester;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReturnRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public ReturnRequest code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getRequestDate() {
        return this.requestDate;
    }

    public ReturnRequest requestDate(Instant requestDate) {
        this.setRequestDate(requestDate);
        return this;
    }

    public void setRequestDate(Instant requestDate) {
        this.requestDate = requestDate;
    }

    public String getNote() {
        return this.note;
    }

    public ReturnRequest note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ReturnRequestStatus getStatus() {
        return this.status;
    }

    public ReturnRequest status(ReturnRequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ReturnRequestStatus status) {
        this.status = status;
    }

    public Employee getRequester() {
        return this.requester;
    }

    public void setRequester(Employee employee) {
        this.requester = employee;
    }

    public ReturnRequest requester(Employee employee) {
        this.setRequester(employee);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((ReturnRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnRequest{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", requestDate='" + getRequestDate() + "'" +
            ", note='" + getNote() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
