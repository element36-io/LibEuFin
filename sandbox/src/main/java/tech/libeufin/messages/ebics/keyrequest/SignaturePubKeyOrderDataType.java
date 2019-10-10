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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3c.dom.Element;


/**
 * Datentyp für Public Key Dateien unabhängig von der Auftragsart / Geschäftsvorfall.
 * 
 * <p>Java class for SignaturePubKeyOrderDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignaturePubKeyOrderDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SignaturePubKeyInfo" type="{http://www.ebics.org/S001}SignaturePubKeyInfoType"/>
 *         &lt;element name="PartnerID" type="{http://www.ebics.org/S001}PartnerIDType"/>
 *         &lt;element name="UserID" type="{http://www.ebics.org/S001}UserIDType"/>
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
@XmlType(name = "SignaturePubKeyOrderDataType", namespace = "http://www.ebics.org/S001", propOrder = {
    "signaturePubKeyInfo",
    "partnerID",
    "userID",
    "any"
})
public class SignaturePubKeyOrderDataType {

    @XmlElement(name = "SignaturePubKeyInfo", required = true)
    protected SignaturePubKeyInfoType signaturePubKeyInfo;
    @XmlElement(name = "PartnerID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String partnerID;
    @XmlElement(name = "UserID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String userID;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the signaturePubKeyInfo property.
     * 
     * @return
     *     possible object is
     *     {@link SignaturePubKeyInfoType }
     *     
     */
    public SignaturePubKeyInfoType getSignaturePubKeyInfo() {
        return signaturePubKeyInfo;
    }

    /**
     * Sets the value of the signaturePubKeyInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignaturePubKeyInfoType }
     *     
     */
    public void setSignaturePubKeyInfo(SignaturePubKeyInfoType value) {
        this.signaturePubKeyInfo = value;
    }

    /**
     * Gets the value of the partnerID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartnerID() {
        return partnerID;
    }

    /**
     * Sets the value of the partnerID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartnerID(String value) {
        this.partnerID = value;
    }

    /**
     * Gets the value of the userID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Sets the value of the userID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserID(String value) {
        this.userID = value;
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
