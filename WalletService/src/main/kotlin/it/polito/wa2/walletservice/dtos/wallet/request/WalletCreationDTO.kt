package it.polito.wa2.walletservice.dtos.wallet.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo


//@JsonTypeInfo(
//    use = JsonTypeInfo.Id.NAME,
//    include = JsonTypeInfo.As.EXISTING_PROPERTY,
//    property = "walletType"
//)
//@JsonSubTypes(
//    value = [
//        JsonSubTypes.Type(value = CustomerWalletCreationDTO::class, name= "CUSTOMER"),
//        JsonSubTypes.Type(value = WarehouseWalletCreationDTO::class, name = "WAREHOUSE")
//    ]
//)
//interface WalletCreationDTO : Emittable{
//    val walletType: String
//}

//TODO: DA FARE PIU AVANTI
