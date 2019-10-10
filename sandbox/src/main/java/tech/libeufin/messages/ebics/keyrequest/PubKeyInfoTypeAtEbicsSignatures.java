//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.10.10 at 06:36:01 PM CEST 
//


package tech.libeufin.messages.ebics.keyrequest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * Datentyp für die Darstellung eines öffentlichen RSA-Schlüssels als Exponent-Modulus-Kombination oder als X509-Zertifikat.
 * 
 * <p>Java class for PubKeyInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PubKeyInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}X509Data" minOccurs="0"/>
 *           &lt;element name="PubKeyValue" type="{http://www.ebics.org/S001}PubKeyValueType"/>
 *         &lt;/sequence>
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
@XmlType(name = "PubKeyInfoType", namespace = "http://www.ebics.org/S001", propOrder = {
    "x509Data",
    "pubKeyValue",
    "any"
})
@XmlSeeAlso({
    SignaturePubKeyInfoType.class
})
public class PubKeyInfoTypeAtEbicsSignatures {

    @XmlElement(name = "X509Data", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected X509DataType x509Data;
    @XmlElement(name = "PubKeyValue", required = true)
    protected PubKeyValueTypeAtEbicsSignatures pubKeyValue;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the x509Data property.
     * 
     * @return
     *     possible object is
     *     {@link X509DataType }
     *     
     */
    public X509DataType getX509Data() {
        return x509Data;
    }

    /**
     * Sets the value of the x509Data property.
     * 
     * @param value
     *     allowed object is
     *     {@link X509DataType }
     *     
     */
    public void setX509Data(X509DataType value) {
        this.x509Data = value;
    }

    /**
     * Gets the value of the pubKeyValue property.
     * 
     * @return
     *     possible object is
     *     {@link PubKeyValueTypeAtEbicsSignatures }
     *     
     */
    public PubKeyValueTypeAtEbicsSignatures getPubKeyValue() {
        return pubKeyValue;
    }

    /**
     * Sets the value of the pubKeyValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link PubKeyValueTypeAtEbicsSignatures }
     *     
     */
    public void setPubKeyValue(PubKeyValueTypeAtEbicsSignatures value) {
        this.pubKeyValue = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

}
