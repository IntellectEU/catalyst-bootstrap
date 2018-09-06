package com.intellecteu.catalyst.swift.transformer.mt;

import com.intellecteu.catalyst.swift.converter.MTUtils;
import org.apache.commons.lang3.StringUtils;
import swift.xsd.fin_103.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MT103Builder {

    public static final int MAX_35_CHARS = 35;
    private final MT103Type fin;

    private MT103Builder() {
        fin = new MT103Type();
    }
    
    public static com.intellecteu.catalyst.swift.transformer.mt.MT103Builder core() {
        return new com.intellecteu.catalyst.swift.transformer.mt.MT103Builder();
    }

    /**
     * Field :20:
     */
    public com.intellecteu.catalyst.swift.transformer.mt.MT103Builder sendersReference(String reference) {
        MT103F20AType f20A = new MT103F20AType();
        f20A.setF20(reference);
        fin.setF20A(f20A);
        return this;
    }

    /**
     * Field :23B:
     * 
     */
    public com.intellecteu.catalyst.swift.transformer.mt.MT103Builder bankOperationCode(String value) {
        return bankOperationCode(BankOperationsCode.valueOf(value));
    }

    /**
     * Field :23B:
     *
     */
    public com.intellecteu.catalyst.swift.transformer.mt.MT103Builder bankOperationCode(BankOperationsCode code) {
        MT103F23A1Type f23A1 = new MT103F23A1Type();
        f23A1.setF23B(Code4Ec211Type.fromValue(code.value()));
        fin.setF23A1(f23A1);
        return this;
    }

    /**
     * Field :32A:
     *
     */
    public com.intellecteu.catalyst.swift.transformer.mt.MT103Builder dateCurrencyAmount(LocalDate date, String currency, String amount) {
        MT103F32AType f32a = new MT103F32AType();
        F32AType value = new F32AType();
        value.setDate(MTUtils.FIN_FORMATTER.format(date));
        value.setAmount(amount);
        value.setCurrency(currency);
        f32a.setF32A(value);
        fin.setF32A(f32a);
        return this;
    }

    /**
     * Field :50A:
     *
     */
    public com.intellecteu.catalyst.swift.transformer.mt.MT103Builder orderingCustomerIdCode(String idCode) {
        return orderingCustomerIdCodeAndAccount(idCode, null);
    }

    /**
     * Field :50A: with account
     *
     */
    public com.intellecteu.catalyst.swift.transformer.mt.MT103Builder orderingCustomerIdCodeAndAccount(String idCode, String account) {
        MT103F50AType orderingCustomer = new MT103F50AType();
        
        F50A2Type f50A = new F50A2Type();
        f50A.setIdentifierCode(idCode);
        if (!StringUtils.isEmpty(account)) {
            f50A.setAccount(account);
        }
        
        orderingCustomer.setF50A(f50A);
        fin.setF50A(orderingCustomer);
        return this;
    }

    /**
     * Field :59:
     *
     */
    public com.intellecteu.catalyst.swift.transformer.mt.MT103Builder beneficiaryCustomerNameAndAddress(String nameAndAddress) {
        Pattern p = Pattern.compile("\\G\\s*(.{1,"+ MAX_35_CHARS +"})(?=\\s|$)", Pattern.DOTALL);
        Matcher m = p.matcher(nameAndAddress.replaceAll("\r", "").replaceAll("\n", ""));

        List<String> list = new ArrayList<>();
        
        while (m.find()) {
            list.add(m.group(1));
        }
        
        return beneficiaryCustomerNameAndAddress(list);
    }

    /**
     * Field :59:
     *
     */
    public com.intellecteu.catalyst.swift.transformer.mt.MT103Builder beneficiaryCustomerNameAndAddress(List<String> nameAndAddress) {
        TextFIN4M35XType nameAndAddressField = new TextFIN4M35XType();
        nameAndAddress.forEach(s -> nameAndAddressField.getLine().add(s));
        
        MT103F59AType f59A = fin.getF59A();
        if (f59A == null) {
           f59A = new MT103F59AType();
        }
        
        F593Type f59 = f59A.getF59();
        if (f59 == null) {
            f59 = new F593Type();
        }

        f59.setNameAndAddress(nameAndAddressField);
        f59A.setF59(f59);
        fin.setF59A(f59A);
        return this;
    }

    /**
     * Field :71A:
     *
     */
    public com.intellecteu.catalyst.swift.transformer.mt.MT103Builder charges(ChargeDetails charges) {
        MT103F71A1Type f71A1 = new MT103F71A1Type();
        f71A1.setF71A(Code3Ea26Type.fromValue(charges.value()));
        fin.setF71A1(f71A1);
        return this;
    }

    /**
     * Field :71A:
     *
     */
    public com.intellecteu.catalyst.swift.transformer.mt.MT103Builder charges(String charges) {
        return charges(ChargeDetails.valueOf(charges));
    }


    /**
     * Build message
     * 
     * @return Object of MT103 body
     */
    public MT103Type build() {
        return fin;
    }

    enum BankOperationsCode {


        /**
         * This message contains a credit transfer where there is no SWIFT Service Level involved.
         *
         */
        CRED,

        /**
         * This message contains a credit transfer for test purposes.
         *
         */
        CRTS,

        /**
         * This message contains a credit transfer to be processed according to the SWIFTPay Service Level.
         *
         */
        SPAY,

        /**
         * This message contains a credit transfer to be processed according to the Priority Service Level.
         *
         */
        SPRI,

        /**
         * This message contains a credit transfer to be processed according to the Standard Service Level.
         *
         */
        SSTD;

        public String value() {
            return name();
        }

    }

    enum ChargeDetails {
        /**
         * All transaction charges are to be borne by the beneficiary customer.
         *
         */
        BEN,

        /**
         * All transaction charges are to be borne by the ordering customer.
         *
         */
        OUR,

        /**
         * All transaction charges other than the charges of the financial institution servicing the ordering customer account are borne by the beneficiary customer.
         *
         */
        SHA;

        public String value() {
            return name();
        }

    }
    
}
