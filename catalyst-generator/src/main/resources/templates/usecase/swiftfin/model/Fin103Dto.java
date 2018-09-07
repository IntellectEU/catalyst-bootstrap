{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Example JSON Object for MT103 transfer
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fin103Dto {

    public enum OperationalCode {
        CRED,
        CRTS,
        SPAY,
        SPRI,
        SSTD;

        public String value() {
            return name();
        }

        public static OperationalCode fromValue(String v) {
            return valueOf(v);
        }
    }

    public enum Charges {

        BEN,
        SHA,
        OUR;

        public String value() {
            return name();
        }

        public static Charges fromValue(String v) {
            return valueOf(v);
        }
    }
    
    protected String senderBIC;
    
    protected String senderLT;
    
    protected String receiverBIC;
    
    protected String receiverLT;
    
    protected String sendersReference;
    
    protected OperationalCode bankOperationalCode;
    
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    protected LocalDate valueDate;
    
    protected String currency;
    
    protected BigDecimal amount;
    
    protected String orderingCustomerBIC;
    protected String orderingCustomerAccount;
    protected String orderingCustomerName;
    protected String orderingCustomerAddressLine1;
    protected String orderingCustomerAddressLine2;
    protected String orderingCustomerCity;
    protected String orderingCustomerCountry;
    
    protected String beneficiaryBIC;
    protected String beneficiaryAccount;
    protected String beneficiaryName;
    protected String beneficiaryAddressLine1;
    protected String beneficiaryAddressLine2;
    protected String beneficiaryCity;
    protected String beneficiaryCountry;
    
    protected Charges detailsOfCharges;
}

<%={{ }}=%>