package it.polito.wa2.warehouseservice.services.interfaces

import it.polito.wa2.warehouseservice.dtos.ProductStockDTO
import it.polito.wa2.warehouseservice.dtos.WarehouseDTO
import it.polito.wa2.warehouseservice.dtos.order.request.ProductWarehouseDTO
import it.polito.wa2.warehouseservice.dtos.order.request.ProductsInWarehouseDTO
import it.polito.wa2.warehouseservice.dtos.order.request.PurchaseProductDTO
import it.polito.wa2.warehouseservice.entities.ProductStock
import it.polito.wa2.warehouseservice.entities.Warehouse
import org.springframework.data.domain.Page
import java.math.BigDecimal

interface WarehouseService {
    fun getWarehouses(productId: Long?, pageNo: Int, pageSize: Int): Page<WarehouseDTO>
    fun getWarehouseById(warehouseId: Long): WarehouseDTO
    fun getWarehouseEntityById(warehouseId: Long) : Warehouse
    fun createWarehouse(warehouseDTO: WarehouseDTO): WarehouseDTO
    fun updateOrCreateWarehouse(warehouseId: Long, warehouseDTO: WarehouseDTO): WarehouseDTO
    fun updateWarehouse(warehouseId: Long, warehouseDTO: WarehouseDTO): WarehouseDTO
    fun deleteWarehouse(warehouseId: Long)
    fun addProductStock(warehouseId: Long, productStockDTO: ProductStockDTO): ProductStockDTO
    fun updateOrAddProductStock(warehouseId: Long, productId: Long, productStockDTO: ProductStockDTO): ProductStockDTO
    fun updateProductStock(warehouseId: Long, productId: Long, productStockDTO: ProductStockDTO): ProductStockDTO
    fun updateQuantityAndRetrieveAmount(purchaseProducts: List<PurchaseProductDTO>) : BigDecimal
    fun getWarehouseHavingProducts(productList: List<PurchaseProductDTO>) : List<ProductWarehouseDTO>
    fun cancelRequestUpdate(productList: List<ProductsInWarehouseDTO>)

}