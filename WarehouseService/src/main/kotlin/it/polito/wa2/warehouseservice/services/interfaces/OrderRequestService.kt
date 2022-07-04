package it.polito.wa2.warehouseservice.services.interfaces

import it.polito.wa2.warehouseservice.dtos.order.request.WarehouseOrderRequestDTO

interface OrderRequestService {

    fun process(orderRequestDTO: WarehouseOrderRequestDTO, id: String)
}