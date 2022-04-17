package it.polito.wa2.warehouseservice.validators

import it.polito.wa2.warehouseservice.dtos.ProductStockDTO

fun ProductStockDTO.validatePost(): Boolean{
    return this.productId != null && this.productQty != null && this.productQty >= 0 && this.alarmLevel != null && this.alarmLevel >= 0
}

fun ProductStockDTO.validatePut(): Boolean{
    return this.productQty != null && this.productQty >= 0 && this.alarmLevel != null && this.alarmLevel >= 0
}

fun ProductStockDTO.validatePatch(): Boolean{
    return (this.productQty != null && this.productQty >= 0) || (this.alarmLevel != null && this.alarmLevel >= 0)
}

