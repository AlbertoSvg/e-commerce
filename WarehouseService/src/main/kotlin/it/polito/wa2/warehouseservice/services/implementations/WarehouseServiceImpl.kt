package it.polito.wa2.warehouseservice.services.implementations

import it.polito.wa2.warehouseservice.constants.Values
import it.polito.wa2.warehouseservice.dtos.WarehouseDTO
import it.polito.wa2.warehouseservice.entities.Warehouse
import it.polito.wa2.warehouseservice.entities.toWarehouseDTO
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

    override fun getWarehouses(productId: Long?, pageNo: Int, pageSize: Int) : Page<WarehouseDTO> {
        val paging = PageRequest.of(pageNo, pageSize)
        var warehouses: Page<Warehouse> = Page.empty() //TODO: rimuovere var+empty eventualmente quando else sarà implementato

        if (productId == null) {
            warehouses = warehouseRepository.findAll(paging)
        }
        //else
        //    val warehouse = productStockRepository.findAllWarehousesByProductId(productId, paging)
        return warehouses.map { it.toWarehouseDTO() }
    }

    override fun getWarehouseById(warehouseId: Long): WarehouseDTO {
        val warehouse = warehouseRepository.findById(warehouseId)
        if (warehouse.isEmpty) throw RuntimeException(Values.WAREHOUSE_NOT_FOUND)
        return warehouse.get().toWarehouseDTO()
    }

    override fun createWarehouse(name: String): WarehouseDTO {
        val warehouse = Warehouse().also { it.name = name }
        return warehouseRepository.save(warehouse).toWarehouseDTO()
    }

    override fun updateOrCreateWarehouse(warehouseId: Long, name: String): WarehouseDTO {
        val warehouseOpt = warehouseRepository.findById(warehouseId)
        if (warehouseOpt.isPresent) {
            warehouseOpt.get().name = name
            return warehouseRepository.save(warehouseOpt.get()).toWarehouseDTO()
        }
        else {
            val warehouse = Warehouse().also {
                it.id = warehouseId
                it.name = name
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

    /* TODO
    override fun updateWarehouse(name: String?): WarehouseDTO {
        if (name != null) {

        }
    }*/

}