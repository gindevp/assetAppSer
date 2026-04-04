package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.Asssettype;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.AssetLine} entity.
 */
@Schema(description = "Dòng tài sản: Laptop Dell, Màn hình 24 inch, ...")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssetLineDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String code;

    @NotNull
    @Size(max = 255)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    private Boolean active;

    @NotNull
    private Asssettype assetType;

    private AssetGroupDTO assetGroup;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Asssettype getAssetType() {
        return assetType;
    }

    public void setAssetType(Asssettype assetType) {
        this.assetType = assetType;
    }

    public AssetGroupDTO getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(AssetGroupDTO assetGroup) {
        this.assetGroup = assetGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssetLineDTO)) {
            return false;
        }

        AssetLineDTO assetLineDTO = (AssetLineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, assetLineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssetLineDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", active='" + getActive() + "'" +
            ", assetType='" + getAssetType() + "'" +
            ", assetGroup=" + getAssetGroup() +
            "}";
    }
}
