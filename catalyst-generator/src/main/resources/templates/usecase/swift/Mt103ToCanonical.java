{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import catalyst.example.canonical.*;
import com.intellecteu.catalyst.swift.converter.CatalystMtXmlConverter;
import com.intellecteu.catalyst.swift.converter.MTMessage;
import com.intellecteu.catalyst.swift.converter.MTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swift.xsd.fin_103.MT103Type;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Component
public class Mt103ToCanonical {
    private CatalystMtXmlConverter converter;

    @Autowired
    public Mt103ToCanonical(CatalystMtXmlConverter converter) {

        this.converter = converter;
    }

    @Handler
    public JAXBElement<PaymentInstruction> transform(String mtMessageString) throws Exception {
        log.info("Transforming MT103: {}", mtMessageString);

        // Parse MT103 to extract fields
        MTMessage mtMessage = converter.parseMt(mtMessageString);
        swift.xsd.fin_103.Document mt103Document = (swift.xsd.fin_103.Document) mtMessage.getBlock4();
        MT103Type mt103 = mt103Document.getMT103();

        // Create canonical and send it to exchange body
        PaymentInstruction canonical = new PaymentInstruction();

        // F20
        canonical.setSendersReference(mt103.getF20A().getF20());

        // F23B
        canonical.setBankOperationalCode(OperationalCode.fromValue(mt103.getF23A1().getF23B().value()));

        // F32A
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = format.parse("20" + mt103.getF32A().getF32A().getDate());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("EET")); // don't forget to set the correct time zone
        canonical.setValueDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
        canonical.setCurrency(mt103.getF32A().getF32A().getCurrency());
        canonical.setAmount(MTUtils.parseAmount(mt103.getF32A().getF32A().getAmount()));

        // F71A
        canonical.setDetailsOfCharges(Charges.fromValue(mt103.getF71A1().getF71A().value()));

        // Ordering Customer, F50A
        OrderingCustomer orderingCustomer = new OrderingCustomer();
        orderingCustomer.setBIC(mt103.getF50A().getF50A().getIdentifierCode());
        orderingCustomer.setAccount(mt103.getF50A().getF50A().getAccount());
        canonical.setOrderingCustomer(orderingCustomer);

        // Build Name and Address for Beneficiary, F59
        Beneficiary beneficiary = new Beneficiary();
        List<String> nameAndAddress = mt103.getF59A().getF59().getNameAndAddress().getLine();
        beneficiary.setName(nameAndAddress.get(0));
        List<String> address = nameAndAddress.subList(1, nameAndAddress.size());
        address.forEach(addressLine -> beneficiary.getAddress().add(addressLine));
        canonical.setBeneficiary(beneficiary);
        //processBeneficiary(mtMessage.getBeneficiary(), mt103Builder);

        canonical.setReceiverBIC(mtMessage.getBlock123().getBlock1().getLogicalTerminalAddress().substring(0,8));
        canonical.setSenderBIC(mtMessage.getBlock123().getBlock2().getDestinationAddress().substring(0, 8));
        canonical.setReceiverLT("X");
        canonical.setSenderLT("X");

        ObjectFactory of = new ObjectFactory();
        JAXBElement<PaymentInstruction> jaxbElement = of.createPayment(canonical);
        
        return jaxbElement;
    }

}

<%={{ }}=%>
