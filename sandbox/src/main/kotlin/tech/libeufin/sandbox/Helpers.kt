/*
 * This file is part of LibEuFin.
 * Copyright (C) 2020 Taler Systems S.A.
 *
 * LibEuFin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3, or
 * (at your option) any later version.
 *
 * LibEuFin is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with LibEuFin; see the file COPYING.  If not, see
 * <http://www.gnu.org/licenses/>
 */

package tech.libeufin.sandbox

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.request.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import tech.libeufin.util.*
import java.math.BigDecimal

/**
 * Helps to communicate Camt values without having
 * to parse the XML each time one is needed.
 */
data class SandboxCamt(
    val camtMessage: String,
    val messageId: String,
    /**
     * That is the number of SECONDS since Epoch.  This
     * value is exactly what goes into the Camt document.
     */
    val creationTime: Long
)

/**
 * Return:
 * - null if the authentication is disabled (during tests, for example).
 *   This facilitates tests because allows requests to lack entirely a
 *   Authorization header.
 * - the name of the authenticated user
 * - throw exception when the authentication fails
 *
 * Note: at this point it is ONLY checked whether the user provided
 * a valid password for the username mentioned in the Authorization header.
 * The actual access to the resources must be later checked by each handler.
 */
fun ApplicationRequest.basicAuth(): String? {
    val withAuth = this.call.ensureAttribute(WITH_AUTH_ATTRIBUTE_KEY)
    if (!withAuth) {
        logger.info("Authentication is disabled - assuming tests currently running.")
        return null
    }
    val credentials = getHTTPBasicAuthCredentials(this)
    if (credentials.first == "admin") {
        // env must contain the admin password, because --with-auth is true.
        val adminPassword: String = this.call.ensureAttribute(ADMIN_PASSWORD_ATTRIBUTE_KEY)
        if (credentials.second != adminPassword) throw unauthorized(
            "Admin authentication failed"
        )
        return credentials.first
    }
    val passwordHash = transaction {
        val customer = getCustomer(credentials.first)
        customer.passwordHash
    }
    if (!CryptoUtil.checkPwOrThrow(credentials.second, passwordHash))
        throw unauthorized("Customer '${credentials.first}' gave wrong credentials")
    return credentials.first
}

fun SandboxAssert(condition: Boolean, reason: String) {
    if (!condition) throw SandboxError(HttpStatusCode.InternalServerError, reason)
}

fun getOrderTypeFromTransactionId(transactionID: String): String {
    val uploadTransaction = transaction {
        EbicsUploadTransactionEntity.findById(transactionID)
    } ?: throw SandboxError(
        /**
         * NOTE: at this point, it might even be the server's fault.
         * For example, if it failed to store a ID earlier.
         */
        HttpStatusCode.NotFound,
        "Could not retrieve order type for transaction: $transactionID"
    )
    return uploadTransaction.orderType
}

fun getHistoryElementFromTransactionRow(dbRow: BankAccountTransactionEntity): RawPayment {
    return RawPayment(
        subject = dbRow.subject,
        creditorIban = dbRow.creditorIban,
        creditorBic = dbRow.creditorBic,
        creditorName = dbRow.creditorName,
        debtorIban = dbRow.debtorIban,
        debtorBic = dbRow.debtorBic,
        debtorName = dbRow.debtorName,
        date = importDateFromMillis(dbRow.date).toDashedDate(),
        amount = dbRow.amount,
        currency = dbRow.currency,
        // The line below produces a value too long (>35 chars),
        // and dbRow makes the document invalid!
        // uid = "${dbRow.pmtInfId}-${it.msgId}"
        uid = dbRow.accountServicerReference,
        direction = dbRow.direction,
        pmtInfId = dbRow.pmtInfId
    )
}

fun getHistoryElementFromTransactionRow(
    dbRow: BankAccountFreshTransactionEntity
): RawPayment {
    return getHistoryElementFromTransactionRow(dbRow.transactionRef)
}

// Need to be called within a transaction {} block.
fun getCustomer(username: String): DemobankCustomerEntity {
    return DemobankCustomerEntity.find {
        DemobankCustomersTable.username eq username
    }.firstOrNull() ?: throw notFound("Customer '${username}' not found")
}

/**
 * Get person name from a customer's username.
 */
fun getPersonNameFromCustomer(ownerUsername: String): String {
    return when (ownerUsername) {
        "admin" -> "admin" // Could be changed to Admin, or some different value.
        "bank" -> "The Bank"
        else -> transaction {
            val ownerCustomer = DemobankCustomerEntity.find(
                DemobankCustomersTable.username eq ownerUsername
            ).firstOrNull() ?: throw internalServerError(
                "Person name of '$ownerUsername' not found"
            )
            ownerCustomer.name ?: "Unknown"
        }
    }
}
fun getFirstDemobank(): DemobankConfigEntity {
  return transaction {
      DemobankConfigEntity.all().firstOrNull() ?: throw SandboxError(
          HttpStatusCode.InternalServerError,
          "Cannot find one demobank, please create one!"
      )
  }
}

fun getDefaultDemobank(): DemobankConfigEntity {
    return transaction {
        DemobankConfigEntity.find {
            DemobankConfigsTable.name eq "default"
        }.firstOrNull()
    } ?: throw SandboxError(
        HttpStatusCode.InternalServerError,
        "Default demobank is missing."
    )
}
fun maybeCreateDefaultDemobank() {
    transaction {
        if (DemobankConfigEntity.all().empty()) {
            DemobankConfigEntity.new {
                currency = "EUR"
                bankDebtLimit = 1000000
                usersDebtLimit = 10000
                allowRegistrations = true
                name = "default"
            }
            // Give one demobank a own bank account, mainly to award
            // customer upon registration.
            BankAccountEntity.new {
                iban = getIban()
                label = "bank" // used by the wire helper
                owner = "bank" // used by the person name finder
                // For now, the model assumes always one demobank
                demoBank = getFirstDemobank()
            }
        }
    }
}

fun wireTransfer(
    debitAccount: String,
    creditAccount: String,
    demobank: String,
    subject: String,
    amount: String // $currency:x.y
): String {
    val args: Triple<BankAccountEntity, BankAccountEntity, DemobankConfigEntity> = transaction {
        val debitAccountDb = BankAccountEntity.find {
            BankAccountsTable.label eq debitAccount
        }.firstOrNull() ?: throw SandboxError(
            HttpStatusCode.NotFound,
            "Debit account '$debitAccount' not found"
        )
        val creditAccountDb = BankAccountEntity.find {
            BankAccountsTable.label eq creditAccount
        }.firstOrNull() ?: throw SandboxError(
            HttpStatusCode.NotFound,
            "Credit account '$creditAccount' not found"
        )
        val demoBank = DemobankConfigEntity.find {
            DemobankConfigsTable.name eq demobank
        }.firstOrNull() ?: throw SandboxError(
            HttpStatusCode.NotFound,
            "Demobank '$demobank' not found"
        )

        Triple(debitAccountDb, creditAccountDb, demoBank)
    }

    /**
     * Only validating the amount.  Actual check on the
     * currency will be done by the callee below.
     */
    val amountObj = parseAmount(amount)
    return wireTransfer(
        debitAccount = args.first,
        creditAccount = args.second,
        demobank = args.third,
        subject = subject,
        amount = amountObj.amount.toPlainString()
    )
}
/**
 * Book a CRDT and a DBIT transaction and return the unique reference thereof.
 *
 * At the moment there is redundancy because all the creditor / debtor details
 * are contained (directly or indirectly) already in the BankAccount parameters.
 *
 * This is kept both not to break the existing tests and to allow future versions
 * where one party of the transaction is not a customer of the running Sandbox.
 */

fun wireTransfer(
    debitAccount: BankAccountEntity,
    creditAccount: BankAccountEntity,
    demobank: DemobankConfigEntity,
    subject: String,
    amount: String,
): String {
    // sanity check on the amount, no currency allowed here.
    val checkAmount = parseDecimal(amount)
    if (checkAmount == BigDecimal.ZERO) throw badRequest("Wire transfers of zero not possible.")
    val timeStamp = getUTCnow().toInstant().toEpochMilli()
    val transactionRef = getRandomString(8)
    transaction {
        BankAccountTransactionEntity.new {
            creditorIban = creditAccount.iban
            creditorBic = creditAccount.bic
            this.creditorName = getPersonNameFromCustomer(creditAccount.owner)
            debtorIban = debitAccount.iban
            debtorBic = debitAccount.bic
            debtorName = getPersonNameFromCustomer(debitAccount.owner)
            this.subject = subject
            this.amount = amount
            this.currency = demobank.currency
            date = timeStamp
            accountServicerReference = transactionRef
            account = creditAccount
            direction = "CRDT"
            this.demobank = demobank
        }
        BankAccountTransactionEntity.new {
            creditorIban = creditAccount.iban
            creditorBic = creditAccount.bic
            this.creditorName = getPersonNameFromCustomer(creditAccount.owner)
            debtorIban = debitAccount.iban
            debtorBic = debitAccount.bic
            debtorName = getPersonNameFromCustomer(debitAccount.owner)
            this.subject = subject
            this.amount = amount
            this.currency = demobank.currency
            date = timeStamp
            accountServicerReference = transactionRef
            account = debitAccount
            direction = "DBIT"
            this.demobank = demobank
        }
    }
    return transactionRef
}

fun getWithdrawalOperation(opId: String): TalerWithdrawalEntity {
    return transaction {
        TalerWithdrawalEntity.find {
            TalerWithdrawalsTable.wopid eq java.util.UUID.fromString(opId)
        }.firstOrNull() ?: throw SandboxError(
            HttpStatusCode.NotFound, "Withdrawal operation $opId not found."
        )
    }
}

fun getBankAccountFromPayto(paytoUri: String): BankAccountEntity {
    val paytoParse = parsePayto(paytoUri)
    return getBankAccountFromIban(paytoParse.iban)
}

fun getBankAccountFromIban(iban: String): BankAccountEntity {
    return transaction {
        BankAccountEntity.find(BankAccountsTable.iban eq iban).firstOrNull()
    } ?: throw SandboxError(
        HttpStatusCode.NotFound,
        "Did not find a bank account for $iban"
    )
}

fun getBankAccountFromLabel(label: String, demobankName: String): BankAccountEntity {
    return transaction {
        val demobank: DemobankConfigEntity = DemobankConfigEntity.find {
            DemobankConfigsTable.name eq demobankName
        }.firstOrNull() ?: throw notFound("Demobank ${demobankName} not found")
        getBankAccountFromLabel(label, demobank)
    }
}
fun getBankAccountFromLabel(label: String, demobank: DemobankConfigEntity): BankAccountEntity {
    return transaction {
        BankAccountEntity.find(
            BankAccountsTable.label eq label and (BankAccountsTable.demoBank eq demobank.id)
        ).firstOrNull() ?: throw SandboxError(
            HttpStatusCode.NotFound,
            "Did not find a bank account for label ${label}"
        )
    }
}

fun getBankAccountFromSubscriber(subscriber: EbicsSubscriberEntity): BankAccountEntity {
    return transaction {
        subscriber.bankAccount ?: throw SandboxError(
            HttpStatusCode.NotFound,
            "Subscriber doesn't have any bank account"
        )
    }
}

fun BankAccountEntity.bonus(amount: String) {
    wireTransfer(
        "bank",
        this.label,
        this.demoBank.name,
        "Sign-up bonus",
        amount
    )
}

fun ensureDemobank(call: ApplicationCall): DemobankConfigEntity {
    return ensureDemobank(call.getUriComponent("demobankid"))
}

private fun ensureDemobank(name: String): DemobankConfigEntity {
    return transaction {
        DemobankConfigEntity.find {
            DemobankConfigsTable.name eq name
        }.firstOrNull() ?: throw internalServerError("Demobank '$name' never created")
    }
}

fun getSandboxConfig(name: String?): DemobankConfigEntity? {
    return transaction {
        if (name == null) {
            DemobankConfigEntity.all().firstOrNull()
        } else {
            DemobankConfigEntity.find {
                DemobankConfigsTable.name eq name
            }.firstOrNull()
        }
    }
}

fun getEbicsSubscriberFromDetails(userID: String, partnerID: String, hostID: String): EbicsSubscriberEntity {
    return transaction {
        EbicsSubscriberEntity.find {
            (EbicsSubscribersTable.userId eq userID) and (EbicsSubscribersTable.partnerId eq partnerID) and
                    (EbicsSubscribersTable.hostId eq hostID)
        }.firstOrNull() ?: throw SandboxError(
            HttpStatusCode.NotFound,
            "Ebics subscriber not found"
        )
    }
}

/**
 * This helper tries to:
 * 1.  Authenticate the client.
 * 2.  Extract the bank account's label from the request's path
 * 3.  Return the bank account DB object if the client has access to it.
 */
fun getBankAccountWithAuth(call: ApplicationCall): BankAccountEntity {
    val username = call.request.basicAuth()
    val accountAccessed = call.getUriComponent("account_name")
    val demobank = ensureDemobank(call)
    val bankAccount = transaction {
        val res = BankAccountEntity.find {
            (BankAccountsTable.label eq accountAccessed).and(
                BankAccountsTable.demoBank eq demobank.id
            )
        }.firstOrNull()
        res
    } ?: throw notFound("Account '$accountAccessed' not found")
    // Check rights.
    if (
        WITH_AUTH
        && (bankAccount.owner != username && username != "admin")
    ) throw forbidden(
        "Customer '$username' cannot access bank account '$accountAccessed'"
    )
    return bankAccount
}