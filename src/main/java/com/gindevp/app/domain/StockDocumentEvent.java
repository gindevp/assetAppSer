package com.gindevp.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lịch sử thao tác trên phiếu nhập kho / xuất kho (khác log HTTP).
 */
@Entity
@Table(name = "stock_document_event")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockDocumentEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Size(max = 50)
    @Column(name = "login", length = 50)
    private String login;

    /** RECEIPT | ISSUE */
    @NotNull
    @Size(max = 20)
    @Column(name = "doc_type", length = 20, nullable = false)
    private String docType;

    @NotNull
    @Column(name = "doc_id", nullable = false)
    private Long docId;

    @NotNull
    @Size(max = 40)
    @Column(name = "action", length = 40, nullable = false)
    private String action;

    @Size(max = 500)
    @Column(name = "summary", length = 500)
    private String summary;

    @Size(max = 2000)
    @Column(name = "detail", length = 2000)
    private String detail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
