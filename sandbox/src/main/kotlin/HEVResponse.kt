package tech.libeufin.sandbox

import tech.libeufin.messages.ebics.hev.HEVResponseDataType
import tech.libeufin.messages.ebics.hev.ObjectFactory
import tech.libeufin.messages.ebics.hev.SystemReturnCodeType
import javax.xml.bind.JAXBElement

/**
 * Convenience wrapper around the main JAXB value.
 */
class HEVResponse(
    returnCode: String,
    reportText: String,
    protocolAndVersion: Array<ProtocolAndVersion>?) {

    constructor(
        returnCode: String,
        reportText: String
    ) : this(returnCode, reportText, null)

    private val value: HEVResponseDataType = {
        val srt = SystemReturnCodeType()
        srt.setReturnCode(returnCode);
        srt.setReportText(reportText);
        val value = HEVResponseDataType();
        value.setSystemReturnCode(srt);

        protocolAndVersion?.forEach {
            val entry = HEVResponseDataType.VersionNumber()
            entry.setProtocolVersion(it.protocol)
            entry.setValue(it.version)
            value.getVersionNumber().add(entry)
        }

        value
    }()

    fun makeHEVResponse(): JAXBElement<HEVResponseDataType> {
        val of = ObjectFactory()
        return of.createEbicsHEVResponse(value)
    }
}
