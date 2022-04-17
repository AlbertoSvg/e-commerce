package it.polito.wa2.warehouseservice.services.implementations

import it.polito.wa2.warehouseservice.constants.Values
import it.polito.wa2.warehouseservice.dtos.ProductStockDTO
import it.polito.wa2.warehouseservice.dtos.WarehouseDTO
import it.polito.wa2.warehouseservice.entities.ProductStock
import it.polito.wa2.warehouseservice.entities.Warehouse
import it.polito.wa2.warehouseservice.repositories.ProductRepository
import it.polito.wa2.warehouseservice.repositories.ProductStockRepository
import it.polito.wa2.warehouseservice.repositories.WarehouseRepository
import it.polito.wa2.warehouseservice.services.interfaces.WarehouseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class WarehouseServiceImpl(): WarehouseService {

    @Autowired
    lateinit var warehouseRepository: WarehouseRepository

    @Autowired
    lateinit var productStockRepository: ProductStockRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    override fun getWarehouses(productId: Long?, pageNo: Int, pageSize: Int) : Page<WarehouseDTO> {
        val paging = PageRequest.of(pageNo, pageSize)
        val warehouses: Page<Warehouse>

        if (productId == null) {
            warehouses = warehouseRepository.findAll(paging)
        }
        else
            warehouses = productStockRepository.findAllByProductId(productId, paging).map { it -> it.warehouse }
        return warehouses.map { it.toWarehouseDTO() }
    }

    override fun getWarehouseById(warehouseId: Long): WarehouseDTO {
        val warehouse = warehouseRepository.findById(warehouseId)
        if (warehouse.isEmpty) throw RuntimeException(Values.WAREHOUSE_NOT_FOUND)
        return warehouse.get().toWarehouseDTO()
    }

    override fun createWarehouse(warehouseDTO: WarehouseDTO): WarehouseDTO {
        val warehouse = Warehouse().also { it.name = warehouseDTO.name }
        return warehouseRepository.save(warehouse).toWarehouseDTO()
    }

    override fun updateOrCreateWarehouse(warehouseId: Long, warehouseDTO: WarehouseDTO): WarehouseDTO {
        val warehouseOpt = warehouseRepository.findById(warehouseId)
        if (warehouseOpt.isPresent) {
            warehouseOpt.get().name = warehouseDTO.name
            return warehouseRepository.save(warehouseOpt.get()).toWarehouseDTO()
        }
        else {
            val warehouse = Warehouse().also {
                it.id = warehouseId
                it.name = warehouseDTO.name
            }
            return warehouseRepository.save(warehouse).toWarehouseDTO()
        }
    }

    override fun deleteWarehouse(warehouseId: Long) {
        if (warehouseRepository.existsById(warehouseId))
            warehouseRepository.deleteById(warehouseId)
        else
            throw RuntimeException(Values.WAREHOUSE_NOT_FOUND)
    }

    override fun updateWarehouse(warehouseId: Long, warehouseDTO: WarehouseDTO): WarehouseDTO {
        val warehouseOpt = warehouseRepository.findById(warehouseId)
        if (warehouseOpt.isEmpty) throw RuntimeException(Values.WAREHOUSE_NOT_FOUND)
        val warehouse = warehouseOpt.get()
        //TODO: se aggiungiamo altri membri al warehouseDTO, aggiungi qui!)
        if (warehouseDTO.name != null) warehouse.name = warehouseDTO.name
        return warehouseRepository.save(warehouse).toWarehouseDTO()
    }

    override fun addProductStock(warehouseId: Long, productStockDTO: ProductStockDTO): ProductStockDTO {
        val warehouseOpt = warehouseRepository.findById(warehouseId)
        if (warehouseOpt.isEmpty) throw RuntimeException(Values.WAREHOUSE_NOT_FOUND)
        val warehouse = warehouseOpt.get()
        val productOpt = productRepository.findById(productStockDTO.productId!!)
        if (productOpt.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        val product = productOpt.get()
        val productStock = ProductStock().also {
            it.product = product
            it.warehouse = warehouse
            it.productQty = productStockDTO.productQty
            it.alarmLevel = productStockDTO.alarmLevel
        }
        return productStockRepository.save(productStock).toProductStockDTO()
    }

    override fun updateOrAddProductStock(
        warehouseId: Long,
        productId: Long,
        productStockDTO: ProductStockDTO
    ): ProductStockDTO {
        val stock: ProductStock

        val warehouseOpt = warehouseRepository.findById(warehouseId)
        if (warehouseOpt.isEmpty) throw RuntimeException(Values.WAREHOUSE_NOT_FOUND)
        val warehouse = warehouseOpt.get()

        val productOpt = productRepository.findById(productId)
        if (productOpt.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        val product = productOpt.get()

        val stockOpt = productStockRepository.findProductStockByWarehouseAndProduct(warehouse, product)
        if (stockOpt.isEmpty) {
            stock = ProductStock().also{
                it.warehouse = warehouse
                it.product = product
                it.alarmLevel = productStockDTO.alarmLevel
                it.productQty = productStockDTO.productQty
            }
        }
        else {
            stock = stockOpt.get()
            stock.productQty = productStockDTO.productQty
            stock.alarmLevel = productStockDTO.alarmLevel
        }
        return productStockRepository.save(stock).toProductStockDTO()
    }

    override fun updateProductStock(
        warehouseId: Long,
        productId: Long,
        productStockDTO: ProductStockDTO
    ): ProductStockDTO {
        val warehouseOpt = warehouseRepository.findById(warehouseId)
        if (warehouseOpt.isEmpty) throw RuntimeException(Values.WAREHOUSE_NOT_FOUND)
        val warehouse = warehouseOpt.get()

        val productOpt = productRepository.findById(productId)
        if (productOpt.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        val product = productOpt.get()

        val stockOpt = productStockRepository.findProductStockByWarehouseAndProduct(warehouse, product)
        if (stockOpt.isEmpty) throw RuntimeException(Values.PRODUCT_STOCK_NOT_FOUND)
        val stock = stockOpt.get()
        if (productStockDTO.productQty != null) stock.productQty = productStockDTO.productQty
        if (productStockDTO.alarmLevel != null) stock.alarmLevel = productStockDTO.alarmLevel

        return productStockRepository.save(stock).toProductStockDTO()

    }
}