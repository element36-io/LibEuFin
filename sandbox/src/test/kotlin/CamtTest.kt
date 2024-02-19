import org.junit.Test
import tech.libeufin.sandbox.buildCamtString
import tech.libeufin.util.RawPayment
import tech.libeufin.util.XMLUtil
import kotlin.test.assertTrue
import java.math.BigDecimal

class CamtTest {

    @Test
    fun validationTest() {
        val payment = RawPayment(
            creditorIban = "GB33BUKB20201222222222",
            creditorName = "Oliver Smith",
            creditorBic = "BUKBGB33",
            debtorIban = "GB33BUKB20201333333333",
            debtorName = "John Doe",
            debtorBic = "BUKBGB33",
            amount = "2",
            currency = "EUR",
            subject = "reimbursement",
            date = "1000-02-02",
            uid = "0",
            direction = "DBIT"
        )
        val xml = buildCamtString(
            53,
            "GB33BUKB20201222222222",
            mutableListOf(payment),
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )
        assertTrue {
            XMLUtil.validateFromString(xml.toString())
        }
    }
}