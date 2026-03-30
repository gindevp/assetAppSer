package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Tồn kho vật tư theo master — không tách từng cái
 */
@Entity
@Table(name = "consumable_stock")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ConsumableStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 0)
    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand;

    @NotNull
    @Min(value = 0)
    @Column(name = "quantity_issued", nullable = false)
    private Integer quantityIssued;

    @Size(max = 500)
    @Column(name = "note", length = 500)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetLine" }, allowSetters = true)
    private AssetItem assetItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ConsumableStock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantityOnHand() {
        return this.quantityOnHand;
    }

    public ConsumableStock quantityOnHand(Integer quantityOnHand) {
        this.setQuantityOnHand(quantityOnHand);
        return this;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public Integer getQuantityIssued() {
        return this.quantityIssued;
    }

    public ConsumableStock quantityIssued(Integer quantityIssued) {
        this.setQuantityIssued(quantityIssued);
        return this;
    }

    public void setQuantityIssued(Integer quantityIssued) {
        this.quantityIssued = quantityIssued;
    }

    public String getNote() {
        return this.note;
    }

    public ConsumableStock note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AssetItem getAssetItem() {
        return this.assetItem;
    }

    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
    }

    public ConsumableStock assetItem(AssetItem assetItem) {
        this.setAssetItem(assetItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConsumableStock)) {
            return false;
        }
        return getId() != null && getId().equals(((ConsumableStock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ConsumableStock{" +
            "id=" + getId() +
            ", quantityOnHand=" + getQuantityOnHand() +
            ", quantityIssued=" + getQuantityIssued() +
            ", note='" + getNote() + "'" +
            "}";
    }
}
