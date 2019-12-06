/*
 * This file is part of LibEuFin.
 * Copyright (C) 2019 Stanisci and Dold.

 * LibEuFin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3, or
 * (at your option) any later version.

 * LibEuFin is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General
 * Public License for more details.

 * You should have received a copy of the GNU Affero General Public
 * License along with LibEuFin; see the file COPYING.  If not, see
 * <http://www.gnu.org/licenses/>
 */


package tech.libeufin.sandbox

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import java.lang.ArithmeticException
import java.math.BigDecimal
import java.security.interfaces.RSAPublicKey
import java.text.DateFormat
import javax.sql.rowset.serial.SerialBlob
import javax.xml.bind.JAXBContext


val logger: Logger = LoggerFactory.getLogger("tech.libeufin.sandbox")

class CustomerNotFound(id: String?) : Exception("Customer ${id} not found")
class BadInputData(inputData: String?) : Exception("Customer provided invalid input data: ${inputData}")
class BadAmount(badValue: Any?) : Exception("Value '${badValue}' is not a valid amount")
class UnacceptableFractional(statusCode: HttpStatusCode, badNumber: BigDecimal) : Exception(
    "Unacceptable fractional part ${badNumber}"
)


fun findCustomer(id: String?): BankCustomerEntity {

    val idN = try {
        id!!.toInt()
    } catch (e: Exception) {
        e.printStackTrace()
        throw BadInputData(id)
    }

    return transaction {

        BankCustomerEntity.findById(idN) ?: throw CustomerNotFound(id)
    }
}

fun findEbicsSubscriber(partnerID: String, userID: String, systemID: String?): EbicsSubscriberEntity? {
    return if (systemID == null) {
        EbicsSubscriberEntity.find {
            (EbicsSubscribersTable.partnerId eq partnerID) and (EbicsSubscribersTable.userId eq userID)
        }
    } else {
        EbicsSubscriberEntity.find {
            (EbicsSubscribersTable.partnerId eq partnerID) and
                    (EbicsSubscribersTable.userId eq userID) and
                    (EbicsSubscribersTable.systemId eq systemID)
        }
    }.firstOrNull()
}


data class Subscriber(
    val partnerID: String,
    val userID: String,
    val systemID: String?,
    val keys: SubscriberKeys
)

data class SubscriberKeys(
    val authenticationPublicKey: RSAPublicKey,
    val encryptionPublicKey: RSAPublicKey,
    val signaturePublicKey: RSAPublicKey
)


data class EbicsHostPublicInfo(
    val hostID: String,
    val encryptionPublicKey: RSAPublicKey,
    val authenticationPublicKey: RSAPublicKey
)


inline fun <reified T> Document.toObject(): T {
    val jc = JAXBContext.newInstance(T::class.java)
    val m = jc.createUnmarshaller()
    return m.unmarshal(this, T::class.java).value
}

fun sampleTransactions() {

}

fun main() {
    dbCreateTables()

    transaction {
        val pairA = CryptoUtil.generateRsaKeyPair(2048)
        val pairB = CryptoUtil.generateRsaKeyPair(2048)
        val pairC = CryptoUtil.generateRsaKeyPair(2048)
        EbicsHostEntity.new {
            hostId = "host01"
            ebicsVersion = "H004"
            authenticationPrivateKey = SerialBlob(pairA.private.encoded)
            encryptionPrivateKey = SerialBlob(pairB.private.encoded)
            signaturePrivateKey = SerialBlob(pairC.private.encoded)
        }


        val customerEntity = BankCustomerEntity.new {
            name = "Mina"
            }

        EbicsSubscriberEntity.new {
            partnerId = "PARTNER1"
            userId = "USER1"
            systemId = null
            state = SubscriberState.NEW
            nextOrderID = 1
            bankCustomer = customerEntity
        }

        for (i in 1..10) {
            BankTransactionEntity.new {
                counterpart = "IBAN"
                amount = Amount(1)
                subject = "transaction $i"
                date = DateTime.now()
                localCustomer = customerEntity
            }

        }
    }

    val server = embeddedServer(Netty, port = 5000) {
        install(CallLogging)
        install(ContentNegotiation) {
            gson {
                setDateFormat(DateFormat.LONG)
                setPrettyPrinting()
            }
        }
        install(StatusPages) {
            exception<Throwable> { cause ->
                logger.error("Exception while handling '${call.request.uri}'", cause)
                call.respondText("Internal server error.", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            }
            exception<ArithmeticException> { cause ->
                logger.error("Exception while handling '${call.request.uri}'", cause)
                call.respondText("Invalid arithmetic attempted.", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            }
        }
        // TODO: add another intercept call that adds schema validation before the response is sent
        intercept(ApplicationCallPipeline.Fallback) {
            if (this.call.response.status() == null) {
                call.respondText("Not found (no route matched).\n", ContentType.Text.Plain, HttpStatusCode.NotFound)
                return@intercept finish()
            }
        }
        routing {

            get("/{id}/balance") {
                val (name, balance) = transaction {
                    val tmp: BankCustomerEntity = findCustomer(call.parameters["id"])

                    var ret = Amount(0)
                    BankTransactionEntity.find {
                        BankTransactionsTable.localCustomer eq tmp.id
                    }.forEach {
                        ret += it.amount
                    }
                    Pair(tmp.name, ret)
                }

                call.respond(
                    CustomerBalance(
                    name = name,
                    balance = "EUR:${balance}"
                    )
                )

                return@get
            }

            get("/") {
                call.respondText("Hello LibEuFin!\n", ContentType.Text.Plain)
            }
            get("/ebics/hosts") {
                val ebicsHosts = transaction {
                    EbicsHostEntity.all().map { it.hostId }
                }
                call.respond(EbicsHostsResponse(ebicsHosts))
            }
            post("/ebics/hosts") {
                val req = call.receive<EbicsHostCreateRequest>()
                transaction {
                    EbicsHostEntity.new {
                        this.ebicsVersion = req.ebicsVersion
                        this.hostId = hostId
                    }
                }
            }
            get("/ebics/hosts/{id}") {
                val resp = transaction {
                    val host = EbicsHostEntity.find { EbicsHostsTable.hostID eq call.parameters["id"]!! }.firstOrNull()
                    if (host == null) null
                    else EbicsHostResponse(host.hostId, host.ebicsVersion)
                }
                if (resp == null) call.respond(
                    HttpStatusCode.NotFound,
                    SandboxError("host not found")
                )
                else call.respond(resp)
            }
            get("/ebics/subscribers") {
                val subscribers = transaction {
                    EbicsSubscriberEntity.all().map { it.id.value.toString() }
                }
                call.respond(EbicsSubscribersResponse(subscribers))
            }
            get("/ebics/subscribers/{id}") {
                val resp = transaction {
                    val id = call.parameters["id"]!!
                    val subscriber = EbicsSubscriberEntity.findById(id.toInt())!!
                    EbicsSubscriberResponse(
                        id,
                        subscriber.partnerId,
                        subscriber.userId,
                        subscriber.systemId,
                        subscriber.state.name
                    )
                }
                call.respond(resp)
            }
            post("/ebicsweb") {
                call.ebicsweb()
            }
        }
    }
    logger.info("Up and running")
    server.start(wait = true)
}
