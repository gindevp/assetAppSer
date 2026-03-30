package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.ReturnRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.ReturnRequest} entity.
 */
@Schema(description = "Yêu cầu thu hồi")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnRequestDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String code;

    @NotNull
    private Instant requestDate;

    @Size(max = 2000)
    private String note;

    @NotNull
    private ReturnRequestStatus status;

    private EmployeeDTO requester;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Instant requestDate) {
        this.requestDate = requestDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ReturnRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnRequestStatus status) {
        this.status = status;
    }

    public EmployeeDTO getRequester() {
        return requester;
    }

    public void setRequester(EmployeeDTO requester) {
        this.requester = requester;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnRequestDTO)) {
            return false;
        }

        ReturnRequestDTO returnRequestDTO = (ReturnRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, returnRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnRequestDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", requestDate='" + getRequestDate() + "'" +
            ", note='" + getNote() + "'" +
            ", status='" + getStatus() + "'" +
            ", requester=" + getRequester() +
            "}";
    }
}
