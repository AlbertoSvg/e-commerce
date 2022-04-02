package it.polito.wa2.warehouseservice.services.interfaces

import it.polito.wa2.warehouseservice.dtos.WarehouseDTO
import org.springframework.data.domain.Page

interface WarehouseService {
    fun getWarehouses(productId: Long?, pageNo: Int, pageSize: Int): Page<WarehouseDTO>
    fun getWarehouseById(warehouseId: Long): WarehouseDTO
    fun createWarehouse(warehouseDTO: WarehouseDTO): WarehouseDTO
    fun updateOrCreateWarehouse(warehouseId: Long, warehouseDTO: WarehouseDTO): WarehouseDTO
    fun updateWarehouse(warehouseId: Long, warehouseDTO: WarehouseDTO): WarehouseDTO
    fun deleteWarehouse(warehouseId: Long)
}