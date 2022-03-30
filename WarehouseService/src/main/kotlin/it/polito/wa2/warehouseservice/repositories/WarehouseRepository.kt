package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.entities.Warehouse
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface WarehouseRepository: PagingAndSortingRepository<Warehouse, Long> {
}