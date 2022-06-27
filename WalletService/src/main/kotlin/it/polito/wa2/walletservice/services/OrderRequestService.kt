package it.polito.wa2.walletservice.services

import it.polito.wa2.saga.dtos.OrderStatusDTO
import it.polito.wa2.walletservice.dtos.order.request.WalletOrderRequestDTO

interface OrderRequestService {

    fun processOrderRequest(orderRequestDTO: WalletOrderRequestDTO): OrderStatusDTO?
    fun process(orderRequestDTO: WalletOrderRequestDTO, id: String)
}