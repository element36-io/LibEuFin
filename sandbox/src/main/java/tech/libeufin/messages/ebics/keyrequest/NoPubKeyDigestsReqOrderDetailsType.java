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
 * Datentyp für OrderDetails im statischen EBICS-Header von ebicsNoPubKeyDigestsRequest.
 * 
 * <p>Java class for NoPubKeyDigestsReqOrderDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NoPubKeyDigestsReqOrderDetailsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{urn:org:ebics:H004}OrderDetailsType">
 *       &lt;sequence>
 *         &lt;element name="OrderType" type="{urn:org:ebics:H004}OrderTBaseType"/>
 *         &lt;element name="OrderAttribute" type="{urn:org:ebics:H004}OrderAttributeBaseType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NoPubKeyDigestsReqOrderDetailsType")
public class NoPubKeyDigestsReqOrderDetailsType
    extends OrderDetailsType
{


}
