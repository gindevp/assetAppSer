package com.gindevp.app.service.dto;

import java.io.Serializable;

/** Tham chiếu tối thiểu cho YC báo mất vật tư. */
public class ConsumableAssignmentRefDTO implements Serializable {

    private Long id;
    private AssetItemDTO assetItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssetItemDTO getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(AssetItemDTO assetItem) {
        this.assetItem = assetItem;
    }
}
