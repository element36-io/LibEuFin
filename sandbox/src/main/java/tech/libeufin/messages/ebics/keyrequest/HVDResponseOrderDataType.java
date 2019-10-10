//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.10.10 at 06:36:01 PM CEST 
//


package tech.libeufin.messages.ebics.keyrequest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * Datentyp für Auftragsdaten für Auftragsart HVD (Antwort: VEU-Status abrufen).
 * 
 * <p>Java class for HVDResponseOrderDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HVDResponseOrderDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DataDigest" type="{urn:org:ebics:H004}DataDigestType"/>
 *         &lt;element name="DisplayFile" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="OrderDataAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OrderDataSize" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="OrderDetailsAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="BankSignature" type="{urn:org:ebics:H004}SignatureType" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="SignerInfo" type="{urn:org:ebics:H004}SignerInfoType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "HVDResponseOrderDataType", propOrder = {
    "dataDigest",
    "displayFile",
    "orderDataAvailable",
    "orderDataSize",
    "orderDetailsAvailable",
    "signerInfo",
    "any"
})
public class HVDResponseOrderDataType {

    @XmlElement(name = "DataDigest", required = true)
    protected DataDigestType dataDigest;
    @XmlElement(name = "DisplayFile", required = true)
    protected byte[] displayFile;
    @XmlElement(name = "OrderDataAvailable")
    protected boolean orderDataAvailable;
    @XmlElement(name = "OrderDataSize", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger orderDataSize;
    @XmlElement(name = "OrderDetailsAvailable")
    protected boolean orderDetailsAvailable;
    @XmlElement(name = "SignerInfo")
    protected List<SignerInfoType> signerInfo;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the dataDigest property.
     * 
     * @return
     *     possible object is
     *     {@link DataDigestType }
     *     
     */
    public DataDigestType getDataDigest() {
        return dataDigest;
    }

    /**
     * Sets the value of the dataDigest property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataDigestType }
     *     
     */
    public void setDataDigest(DataDigestType value) {
        this.dataDigest = value;
    }

    /**
     * Gets the value of the displayFile property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDisplayFile() {
        return displayFile;
    }

    /**
     * Sets the value of the displayFile property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDisplayFile(byte[] value) {
        this.displayFile = value;
    }

    /**
     * Gets the value of the orderDataAvailable property.
     * 
     */
    public boolean isOrderDataAvailable() {
        return orderDataAvailable;
    }

    /**
     * Sets the value of the orderDataAvailable property.
     * 
     */
    public void setOrderDataAvailable(boolean value) {
        this.orderDataAvailable = value;
    }

    /**
     * Gets the value of the orderDataSize property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOrderDataSize() {
        return orderDataSize;
    }

    /**
     * Sets the value of the orderDataSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOrderDataSize(BigInteger value) {
        this.orderDataSize = value;
    }

    /**
     * Gets the value of the orderDetailsAvailable property.
     * 
     */
    public boolean isOrderDetailsAvailable() {
        return orderDetailsAvailable;
    }

    /**
     * Sets the value of the orderDetailsAvailable property.
     * 
     */
    public void setOrderDetailsAvailable(boolean value) {
        this.orderDetailsAvailable = value;
    }

    /**
     * Gets the value of the signerInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the signerInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSignerInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SignerInfoType }
     * 
     * 
     */
    public List<SignerInfoType> getSignerInfo() {
        if (signerInfo == null) {
            signerInfo = new ArrayList<SignerInfoType>();
        }
        return this.signerInfo;
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
