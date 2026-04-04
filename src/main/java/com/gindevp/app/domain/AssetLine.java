package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.app.domain.enumeration.Asssettype;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Dòng tài sản: Laptop Dell, Màn hình 24 inch, ...
 */
@Entity
@Table(name = "asset_line")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssetLine implements Serializable {

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
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    /** Thiết bị / Vật tư — nguồn chính cho phân loại (không dùng loại ở cấp nhóm cho nghiệp vụ mới). */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false, length = 20)
    private Asssettype assetType = Asssettype.DEVICE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetType" }, allowSetters = true)
    private AssetGroup assetGroup;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    @PrePersist
    @PreUpdate
    protected void applyLineAssetTypeDefault() {
        if (assetType == null) {
            assetType = Asssettype.DEVICE;
        }
    }

    public Long getId() {
        return this.id;
    }

    public AssetLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public AssetLine code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public AssetLine name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public AssetLine description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return this.active;
    }

    public AssetLine active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Asssettype getAssetType() {
        return this.assetType;
    }

    public void setAssetType(Asssettype assetType) {
        this.assetType = assetType;
    }

    public AssetLine assetType(Asssettype assetType) {
        this.setAssetType(assetType);
        return this;
    }

    public AssetGroup getAssetGroup() {
        return this.assetGroup;
    }

    public void setAssetGroup(AssetGroup assetGroup) {
        this.assetGroup = assetGroup;
    }

    public AssetLine assetGroup(AssetGroup assetGroup) {
        this.setAssetGroup(assetGroup);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssetLine)) {
            return false;
        }
        return getId() != null && getId().equals(((AssetLine) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssetLine{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", active='" + getActive() + "'" +
            ", assetType='" + getAssetType() + "'" +
            "}";
    }
}
