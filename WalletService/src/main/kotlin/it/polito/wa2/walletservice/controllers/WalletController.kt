package it.polito.wa2.walletservice.controllers

import it.polito.wa2.walletservice.costants.Strings.CUSTOMER_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.DESTINATION_WALLET_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.INSUFFICIENT_CREDIT
import it.polito.wa2.walletservice.costants.Strings.INTERNAL_SERVER_ERROR
import it.polito.wa2.walletservice.costants.Strings.INVALID_TRANSACTION
import it.polito.wa2.walletservice.costants.Strings.RESOURCE_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.SENDER_WALLET_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.TRANSACTION_EXECUTION_FAILED
import it.polito.wa2.walletservice.costants.Strings.TRANSACTION_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.UNAUTHORIZED_USER
import it.polito.wa2.walletservice.costants.Strings.WALLET_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.WRONG_PARAMETERS
import it.polito.wa2.walletservice.dtos.transaction.TransactionDTO
import it.polito.wa2.walletservice.dtos.transaction.request.RechargeTransactionDTO
import it.polito.wa2.walletservice.dtos.wallet.WalletDTO
import it.polito.wa2.walletservice.entities.WalletType
import it.polito.wa2.walletservice.entities.toWalletDTO
import it.polito.wa2.walletservice.services.WalletService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.validation.Valid


@RestController
@RequestMapping("/wallets")
class WalletController {

    @Autowired
    private lateinit var walletService: WalletService

    /**
     *  ### Description:
     *  Create a new wallet for a given customer. In the Request Body there will be the
     *  Customer’s ID for which you want to create a wallet. The wallet created will initially have no money.
     *  Once the wallet is created, return a 201 (CREATED) response status and the wallet itself as the
     *  response body
     *
     *  ### Mapping:
     *  /wallet
     *
     * @param customerId is the key (String) and the customer ID is the value (Long)
     * @return wallet DTO and status 201 (CREATED)
     */
    @PostMapping
    fun createWalletByCustomerID(
        @RequestBody body: Map<String, Long>
    ): ResponseEntity<Any> {
        try {
            val customerId = body["customerId"]
            return ResponseEntity.status(HttpStatus.CREATED).body(walletService.addWalletToCustomer(customerId!!))
        } catch (e: Exception) {
            return ResponseEntity.internalServerError().body(INTERNAL_SERVER_ERROR)
        }
    }

    /**
     *  ### Description:
     *  Get the details of a wallet. The response body will be the requested wallet, and the response status 200 (ok)
     *
     *  ### Mapping:
     *  /wallet/{walletId}
     *
     * @param walletId the wallet id (Long)
     * @return walletDTO and status 200 (OK)
     */
    @GetMapping("/{walletId}")
    fun getWalletById(
        @PathVariable("walletId") walletId: Long,
        @RequestHeader("userId") userId: String?,
        @RequestHeader("roles") roles: String?
    ): ResponseEntity<Any> {
        try {
            val walletDTO = walletService.getWalletById(walletId, userId, roles).toWalletDTO()
            return ResponseEntity.ok(walletDTO)
        } catch (e: RuntimeException) {
            if (e.message == UNAUTHORIZED_USER)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_USER)
            else
                return ResponseEntity.badRequest().body(WRONG_PARAMETERS)
        }
    }

    @PostMapping("/{walletId}/transactions") //ADMIN ONLY
    fun rechargeTransaction(
        @PathVariable("walletId") receiverWalletId: Long,
        @RequestBody @Valid transaction: RechargeTransactionDTO,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        try {
            return if (bindingResult.hasErrors()) ResponseEntity.badRequest().body(WRONG_PARAMETERS)
            else {
                val returnedTransactionDTO =
                    walletService.rechargeTransaction(receiverWalletId, transaction)
                ResponseEntity.status(HttpStatus.CREATED).body(returnedTransactionDTO)
            }
        } catch (e: RuntimeException) {
            return if (e.message == SENDER_WALLET_NOT_FOUND)
                ResponseEntity.badRequest().body(SENDER_WALLET_NOT_FOUND)
            else if (e.message == DESTINATION_WALLET_NOT_FOUND)
                ResponseEntity.badRequest().body(DESTINATION_WALLET_NOT_FOUND)
            else if (e.message == INSUFFICIENT_CREDIT)
                ResponseEntity.badRequest().body(INSUFFICIENT_CREDIT)
            else ResponseEntity.badRequest().body(TRANSACTION_EXECUTION_FAILED)
        }
    }


    /**
     *  ### Description:
     *  Get a list of transactions regarding a given wallet in a given time frame.
     *
     *  ### Mapping:
     *  /wallet/{walletId}/transactions?from=dateInMillis&to=dateInMillis.
     *
     *  ### Important:
     *  dateInMillis must respect the pattern "yyyy-MM-dd HH:mm:ss.SSS"
     *
     *  @param senderWalletId the sender wallet id
     *  @param from must have the pattern = "yyyy-MM-dd HH:mm:ss.SSS" (LocalDateTime)
     *  @param to must have the pattern = "yyyy-MM-dd HH:mm:ss.SSS" (LocalDateTime)
     *  @param pageNo the number of the requested page
     *  @param size the number of records per page
     *
     *  @return list of TransactionDTO, a transactionPageDTO (with the information about the total number of pages,
     *  the total number of items and the current page number) and status 200 (OK)
     */
    @GetMapping("/{walletId}/transactions")
    fun getTransactionsByDateRange(
        @PathVariable("walletId") senderWalletId: Long,
        @RequestParam(name = "pageNo", defaultValue = "0") pageNo: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
        @RequestParam(name = "from") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") from: LocalDateTime,
        @RequestParam(name = "to") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") to: LocalDateTime
    ): ResponseEntity<Any> {
        // ESEMPIO DI GET:
        // GET http://localhost:8090/wallets/3/transactions?pageNo=0&size=2&from=2021-04-13 20:32:09.877&to=2021-04-13 20:35:47.000
        return try {
            val transactionPageDTO = walletService.getTransactionsByDateRange(senderWalletId, from, to, pageNo, size)
            val response = hashMapOf<String, Any>()

            response["Transactions"] = transactionPageDTO.content
            response["currentPage"] = pageNo
            response["totalItems"] = transactionPageDTO.totalElements
            response["totalPages"] = transactionPageDTO.totalPages

            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().body(RESOURCE_NOT_FOUND)
        }
    }

    /**
     *  ### Description:
     *  Get the details of a single transaction referring to a particular wallet ID.
     *
     *  ### Mapping:
     *  /wallet//{walletId}/transactions/{transactionId}
     *
     * @param walletId the target wallet id (Long)
     * @param transactionId the transaction id (Long)
     *
     * @return transactionDTO and status OK (200)
     */
    @GetMapping("/{walletId}/transactions/{transactionId}")
    fun getTransactionById(
        @PathVariable("walletId") walletId: Long,
        @PathVariable("transactionId") transactionId: Long,
    ): ResponseEntity<Any> {
        return try {
            val transactionDTO = walletService.getTransaction(walletId, transactionId)
            ResponseEntity.ok(transactionDTO)
        } catch (e: RuntimeException) {
            if (e.message == TRANSACTION_NOT_FOUND) ResponseEntity.badRequest().body(TRANSACTION_NOT_FOUND)
            else if (e.message == WALLET_NOT_FOUND) ResponseEntity.badRequest().body(WALLET_NOT_FOUND)
            else ResponseEntity.badRequest().body(INVALID_TRANSACTION)
        }
    }
}
