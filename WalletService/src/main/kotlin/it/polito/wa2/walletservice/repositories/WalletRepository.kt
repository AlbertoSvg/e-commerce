package it.polito.wa2.walletservice.repositories

import it.polito.wa2.walletservice.entities.Wallet
import it.polito.wa2.walletservice.entities.WalletType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface WalletRepository : CrudRepository<Wallet, Long> {

    /** ### Description:
     * Fetch all the wallets in the db
     * @return A MutableIterable of Wallet.
     */
    override fun findAll(): MutableIterable<Wallet>

    /** ### Description:
     * Find a wallet using the ID number
     * @param id The id of the wallet (Long)
     * @return A container object (of Wallet) which may or may not contain a non-null value.
     */
    override fun findById(id: Long): Optional<Wallet>

    fun findByOwner(owner: Long): Optional<Wallet>

    @Transactional(readOnly = true)
    fun findByIdAndOwnerAndWalletType(id:Long, owner: Long, walletType: WalletType):Wallet?

    @Transactional(readOnly = true)
    fun findByWalletTypeAndOwner(walletType: WalletType, owner: Long): Wallet?
}
