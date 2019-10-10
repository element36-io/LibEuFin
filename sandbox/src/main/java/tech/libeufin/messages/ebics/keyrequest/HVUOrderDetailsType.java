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
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3c.dom.Element;


/**
 * Datentyp für HVU-Auftragsdetails.
 * 
 * <p>Java class for HVUOrderDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HVUOrderDetailsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrderType" type="{urn:org:ebics:H004}OrderTBaseType"/>
 *         &lt;element name="FileFormat" type="{urn:org:ebics:H004}FileFormatType" minOccurs="0"/>
 *         &lt;element name="OrderID" type="{urn:org:ebics:H004}OrderIDType"/>
 *         &lt;element name="OrderDataSize" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="SigningInfo" type="{urn:org:ebics:H004}HVUSigningInfoType"/>
 *         &lt;element name="SignerInfo" type="{urn:org:ebics:H004}SignerInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="OriginatorInfo" type="{urn:org:ebics:H004}HVUOriginatorInfoType"/>
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
@XmlType(name = "HVUOrderDetailsType", propOrder = {
    "orderType",
    "fileFormat",
    "orderID",
    "orderDataSize",
    "signingInfo",
    "signerInfo",
    "originatorInfo",
    "any"
})
public class HVUOrderDetailsType {

    @XmlElement(name = "OrderType", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String orderType;
    @XmlElement(name = "FileFormat")
    protected FileFormatType fileFormat;
    @XmlElement(name = "OrderID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String orderID;
    @XmlElement(name = "OrderDataSize", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger orderDataSize;
    @XmlElement(name = "SigningInfo", required = true)
    protected HVUSigningInfoType signingInfo;
    @XmlElement(name = "SignerInfo")
    protected List<SignerInfoType> signerInfo;
    @XmlElement(name = "OriginatorInfo", required = true)
    protected HVUOriginatorInfoType originatorInfo;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the orderType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     * Sets the value of the orderType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderType(String value) {
        this.orderType = value;
    }

    /**
     * Gets the value of the fileFormat property.
     * 
     * @return
     *     possible object is
     *     {@link FileFormatType }
     *     
     */
    public FileFormatType getFileFormat() {
        return fileFormat;
    }

    /**
     * Sets the value of the fileFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileFormatType }
     *     
     */
    public void setFileFormat(FileFormatType value) {
        this.fileFormat = value;
    }

    /**
     * Gets the value of the orderID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderID() {
        return orderID;
    }

    /**
     * Sets the value of the orderID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderID(String value) {
        this.orderID = value;
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
     * Gets the value of the signingInfo property.
     * 
     * @return
     *     possible object is
     *     {@link HVUSigningInfoType }
     *     
     */
    public HVUSigningInfoType getSigningInfo() {
        return signingInfo;
    }

    /**
     * Sets the value of the signingInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link HVUSigningInfoType }
     *     
     */
    public void setSigningInfo(HVUSigningInfoType value) {
        this.signingInfo = value;
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
     * Gets the value of the originatorInfo property.
     * 
     * @return
     *     possible object is
     *     {@link HVUOriginatorInfoType }
     *     
     */
    public HVUOriginatorInfoType getOriginatorInfo() {
        return originatorInfo;
    }

    /**
     * Sets the value of the originatorInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link HVUOriginatorInfoType }
     *     
     */
    public void setOriginatorInfo(HVUOriginatorInfoType value) {
        this.originatorInfo = value;
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
