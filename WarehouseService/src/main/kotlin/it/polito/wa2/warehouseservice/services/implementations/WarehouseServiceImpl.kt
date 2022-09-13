package it.polito.wa2.warehouseservice.services.implementations

import it.polito.wa2.saga.costants.Topics
import it.polito.wa2.saga.dtos.MailDTO
import it.polito.wa2.saga.services.MessageService
import it.polito.wa2.warehouseservice.constants.Values
import it.polito.wa2.warehouseservice.constants.Values.PRODUCT_STOCK_NOT_FOUND
import it.polito.wa2.warehouseservice.dtos.ProductStockDTO
import it.polito.wa2.warehouseservice.dtos.WarehouseDTO
import it.polito.wa2.warehouseservice.dtos.order.request.ProductWarehouseDTO
import it.polito.wa2.warehouseservice.dtos.order.request.ProductsInWarehouseDTO
import it.polito.wa2.warehouseservice.dtos.order.request.PurchaseProductDTO
import it.polito.wa2.warehouseservice.entities.ProductStock
import it.polito.wa2.warehouseservice.entities.Warehouse
import it.polito.wa2.warehouseservice.repositories.ProductRepository
import it.polito.wa2.warehouseservice.repositories.ProductStockRepository
import it.polito.wa2.warehouseservice.repositories.WarehouseRepository
import it.polito.wa2.warehouseservice.services.interfaces.ProductService
import it.polito.wa2.warehouseservice.services.interfaces.WarehouseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal


@Service
@Transactional
class WarehouseServiceImpl(): WarehouseService {

    @Autowired
    lateinit var warehouseRepository: WarehouseRepository

    @Autowired
    lateinit var productStockRepository: ProductStockRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var productService: ProductService

    @Autowired
    lateinit var messageService: MessageService

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

    override fun getWarehouseEntityById(warehouseId: Long): Warehouse {
        val warehouse = warehouseRepository.findById(warehouseId)
        if (warehouse.isEmpty) throw RuntimeException(Values.WAREHOUSE_NOT_FOUND)
        return warehouse.get()
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
        val warehouse = getWarehouseEntityById(warehouseId)
        //TODO: se aggiungiamo altri membri al warehouseDTO, aggiungi qui!)
        if (warehouseDTO.name != null) warehouse.name = warehouseDTO.name
        return warehouseRepository.save(warehouse).toWarehouseDTO()
    }

    override fun addProductStock(warehouseId: Long, productStockDTO: ProductStockDTO): ProductStockDTO {
        val warehouse = getWarehouseEntityById(warehouseId)
        val product = productService.getProductEntityById(productStockDTO.productId!!)
        val productStock = ProductStock().also {
            it.product = product
            it.warehouse = warehouse
            it.productQty = productStockDTO.productQty
            it.alarmLevel = productStockDTO.alarmLevel
        }
        return productStockRepository.save(productStock).toProductStockDTO()
    }

    private fun sendNotification(productStock: ProductStock){
        val mailBody = "Hi admin,\n" +
                "in the warehouse ${productStock.warehouse?.name} " +
                "the quantity of a product ${productStock.product?.id} is below the alarm level."

        val mailDTO = MailDTO(
            "0",
            null,
            "Alarm notification",
            mailBody
        )

        messageService.publish(mailDTO, "Alarm notification", Topics.mailTopic)
    }
    override fun updateOrAddProductStock(
        warehouseId: Long,
        productId: Long,
        productStockDTO: ProductStockDTO
    ): ProductStockDTO {
        println("AAAAAAAAAAAAAAAAAAAAA")
        val stock: ProductStock

        val warehouse = getWarehouseEntityById(warehouseId)
        val product = productService.getProductEntityById(productId)

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

        if (stock.productQty!! <= stock.alarmLevel!!)
            sendNotification(stock)

        return productStockRepository.save(stock).toProductStockDTO()
    }

    override fun updateProductStock(
        warehouseId: Long,
        productId: Long,
        productStockDTO: ProductStockDTO
    ): ProductStockDTO {
        val warehouse = getWarehouseEntityById(warehouseId)
        val product = productService.getProductEntityById(productId)

        val stockOpt = productStockRepository.findProductStockByWarehouseAndProduct(warehouse, product)
        if (stockOpt.isEmpty) throw RuntimeException(PRODUCT_STOCK_NOT_FOUND)
        val stock = stockOpt.get()
        if (productStockDTO.productQty != null) stock.productQty = productStockDTO.productQty
        if (productStockDTO.alarmLevel != null) stock.alarmLevel = productStockDTO.alarmLevel

        if (stock.productQty!! <= stock.alarmLevel!!)
            sendNotification(stock)
        return productStockRepository.save(stock).toProductStockDTO()

    }

    override fun getProductStocks() : List<ProductStockDTO> {
       return productStockRepository.findAll().map { it.toProductStockDTO() }
    }

    private fun updateProductStockQuantity(productStock: ProductStock, newQuantity: Long) : ProductStock{
        productStock.productQty = newQuantity
        if (newQuantity <= productStock.alarmLevel!!)
            sendNotification(productStock)
        return productStock
    }

    override fun updateQuantityAndRetrieveAmount(purchaseProducts: List<PurchaseProductDTO>): BigDecimal {
        var price = BigDecimal("0")

        purchaseProducts.forEach{ it ->
            val product = productService.getProductEntityById(it.productId)
            val stocks = productStockRepository.findAllByProductAndProductQtyIsGreaterThanEqual(product, it.amount.toLong())
            if (stocks.isEmpty())
                throw  RuntimeException(PRODUCT_STOCK_NOT_FOUND)
            val chosenStock = stocks.maxByOrNull { stock -> stock.productQty!! - stock.alarmLevel!! }!!
            updateProductStockQuantity(chosenStock, chosenStock.productQty!! - it.amount)

            price = price.plus(product.price!! * BigDecimal(it.amount))
        }
        return price
    }

    override fun getWarehouseHavingProducts(productList: List<PurchaseProductDTO>): List<ProductWarehouseDTO> {
        val list = mutableListOf<ProductWarehouseDTO>()

        productList.forEach{
            val product = productService.getProductEntityById(it.productId)
            val stocks = productStockRepository.findAllByProductAndProductQtyIsGreaterThanEqual(product, it.amount.toLong())
            if (stocks.isEmpty())
                throw  RuntimeException(PRODUCT_STOCK_NOT_FOUND)
            val chosenStock = stocks.maxByOrNull { stock -> stock.productQty!! - stock.alarmLevel!! }
                ?: throw RuntimeException(PRODUCT_STOCK_NOT_FOUND)
            val productWarehouse = ProductWarehouseDTO(chosenStock.warehouse!!.id!!, chosenStock.product!!.id!!)
            list.add(productWarehouse)
        }
        println(list)
        return list
    }

    override fun cancelRequestUpdate(productList: List<ProductsInWarehouseDTO>) {
        productList.forEach{ productsInWarehouse ->
            productsInWarehouse.purchasedProducts.forEach{ purchasedProduct ->
                val warehouse = getWarehouseEntityById(productsInWarehouse.warehouseId!!)
                val product = productService.getProductEntityById(purchasedProduct.productId)
                val stockOpt =  productStockRepository.findProductStockByWarehouseAndProduct(warehouse,product)
                if (  stockOpt.isEmpty ) {
                    val newStock = ProductStock().also {
                        it.warehouse = warehouse
                        it.product = product
                        it.productQty = purchasedProduct.amount.toLong()
                        it.alarmLevel = purchasedProduct.amount.toLong()
                    }

                    productStockRepository.save(newStock)
                } else {
                    //  updateStockQuantity(stock, stock.quantity + item.amount) // questo invia la mail
                    val stock = stockOpt.get()
                    stock.productQty = stock.productQty!! + purchasedProduct.amount.toLong()
                    productStockRepository.save(stock)
                }
            }
        }
    }

}