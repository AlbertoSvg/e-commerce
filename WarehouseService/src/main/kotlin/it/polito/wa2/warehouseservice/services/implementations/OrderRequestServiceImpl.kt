package it.polito.wa2.warehouseservice.services.implementations

import it.polito.wa2.warehouseservice.dtos.order.request.WarehouseOrderRequestDTO
import it.polito.wa2.saga.services.MessageService
import it.polito.wa2.saga.services.ProcessingLogService
import it.polito.wa2.warehouseservice.dtos.order.request.OrderDetailsDTO
import it.polito.wa2.warehouseservice.dtos.order.request.OrderStatusDTO
import it.polito.wa2.warehouseservice.dtos.order.request.WalletOrderRequestDTO
import it.polito.wa2.warehouseservice.services.interfaces.OrderRequestService
import it.polito.wa2.warehouseservice.services.interfaces.ProductService
import it.polito.wa2.warehouseservice.services.interfaces.WarehouseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
@Transactional
class OrderRequestServiceImpl : OrderRequestService {

    @Autowired
    lateinit var warehouseService: WarehouseService

    @Autowired
    lateinit var productService: ProductService

    @Autowired
    lateinit var processingLogService: ProcessingLogService

    @Autowired
    lateinit var messageService: MessageService

    override fun process(orderRequestDTO: WarehouseOrderRequestDTO, id: String) {
        val uuid = UUID.fromString(id)
        if(processingLogService.isProcessed(uuid))
            return

        var orderDetails: OrderDetailsDTO? = null
        var paymentRequest: WalletOrderRequestDTO? = null
        var orderStatusDTO: OrderStatusDTO? = null
        TODO("Not yet implemented")
    }
}