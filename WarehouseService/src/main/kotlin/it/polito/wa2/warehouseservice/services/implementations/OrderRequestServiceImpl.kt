package it.polito.wa2.warehouseservice.services.implementations

import it.polito.wa2.warehouseservice.dtos.order.request.WarehouseOrderRequestDTO
import it.polito.wa2.warehouseservice.services.interfaces.OrderRequestService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class OrderRequestServiceImpl : OrderRequestService {
    override fun process(orderRequestDTO: WarehouseOrderRequestDTO, id: String) {
        TODO("Not yet implemented")
    }
}