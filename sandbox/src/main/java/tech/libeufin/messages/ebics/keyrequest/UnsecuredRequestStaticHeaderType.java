//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.10.10 at 06:36:01 PM CEST 
//


package tech.libeufin.messages.ebics.keyrequest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Datentyp für den statischen EBICS-Header bei ungesicherten Sendeauftragsarten (Aufträge HIA und INI): kein Nonce, kein Timestamp, keine EU-Datei, keine X001 Authentifizierung, keine Verschlüsselung, keine Digests der öffentlichen Bankschlüssel, Nutzdaten komprimiert, Auftragsattribut DZNNN
 * 
 * <p>Java class for UnsecuredRequestStaticHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnsecuredRequestStaticHeaderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{urn:org:ebics:H004}StaticHeaderBaseType">
 *       &lt;sequence>
 *         &lt;element name="HostID" type="{urn:org:ebics:H004}HostIDType"/>
 *         &lt;element name="Nonce" type="{urn:org:ebics:H004}NonceType" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="Timestamp" type="{urn:org:ebics:H004}TimestampType" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="PartnerID" type="{urn:org:ebics:H004}PartnerIDType"/>
 *         &lt;element name="UserID" type="{urn:org:ebics:H004}UserIDType"/>
 *         &lt;element name="SystemID" type="{urn:org:ebics:H004}UserIDType" minOccurs="0"/>
 *         &lt;element name="Product" type="{urn:org:ebics:H004}ProductElementType" minOccurs="0"/>
 *         &lt;element name="OrderDetails" type="{urn:org:ebics:H004}UnsecuredReqOrderDetailsType"/>
 *         &lt;element name="SecurityMedium" type="{urn:org:ebics:H004}SecurityMediumType"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnsecuredRequestStaticHeaderType")
public class UnsecuredRequestStaticHeaderType
    extends StaticHeaderBaseType
{


}
