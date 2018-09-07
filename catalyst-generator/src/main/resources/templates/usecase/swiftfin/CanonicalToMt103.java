{{=<% %>=}}

<%license%>

package <%fullPackageName%>;
import catalyst.example.canonical.Beneficiary;
import catalyst.example.canonical.OrderingCustomer;
import catalyst.example.canonical.PaymentInstruction;
import com.intellecteu.catalyst.swift.converter.CatalystMtXmlConverter;
import com.intellecteu.catalyst.swift.converter.MTMessage;
import com.intellecteu.catalyst.swift.converter.MTUtils;
import <%fullPackageName%>.mt.Block1Builder;
import <%fullPackageName%>.mt.Block2Builder;
import <%fullPackageName%>.mt.MT103Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swift.xsd.fin_103.Document;
import swift.xsd.fin_103.MT103Type;
import swift.xsd.mtmsg.FinMessageType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Component
public class CanonicalToMt103 {
    private CatalystMtXmlConverter converter;

    @Autowired
    public CanonicalToMt103(CatalystMtXmlConverter converter) {

        this.converter = converter;
    }
    
    @Handler
    public String transform(PaymentInstruction canonical) throws Exception {
        log.info("Transforming canonical: {}", canonical);

        // Get Value Date and Amount for Field :32A:
        LocalDate valueDate = canonical.getValueDate().toGregorianCalendar().toZonedDateTime().toLocalDate();
        String amount = MTUtils.parseAmount(canonical.getAmount());

        // Create MT103 Builder
        MT103Builder mt103Builder = MT103Builder.core()
                // F20
                .sendersReference(canonical.getSendersReference())
                // F23B
                .bankOperationCode(canonical.getBankOperationalCode().value())
                // F32A
                .dateCurrencyAmount(valueDate, canonical.getCurrency(), amount)
                // F71A
                .charges(canonical.getDetailsOfCharges().value());

        // Ordering Customer, F50A
        processOrderingCustomer(canonical.getOrderingCustomer(), mt103Builder);

        // Build Name and Address for Beneficiary, F59
        processBeneficiary(canonical.getBeneficiary(), mt103Builder);
        
        // Build MT103 body (Block 4)
        MT103Type mt103 = mt103Builder.build();
        
        // Create JAXB representation
        Document mt103Document = new Document();
        mt103Document.setMT103(mt103);

        // Create Blocks 1 and 2
        FinMessageType finMessageType = new FinMessageType();
        finMessageType.setBlock1(Block1Builder.forBIC(canonical.getSenderBIC()).build());
        finMessageType.setBlock2(Block2Builder.input(canonical.getReceiverBIC()).build());

        // Create MTMessage and send it to exchange body
        MTMessage mtMessage = new MTMessage(finMessageType);
        mtMessage.setBlock4(mt103Document);

        // Convert to RJE
        return converter.printMt(mtMessage);
    }

    private void processOrderingCustomer(OrderingCustomer orderingCustomer, MT103Builder mt103Builder) {
        String bic = orderingCustomer.getBIC();
        String account = orderingCustomer.getAccount();

        if ( isNotEmpty(bic) && isNotEmpty(account) ) {
            mt103Builder.orderingCustomerIdCodeAndAccount(bic, account);
        } else if (isNotEmpty(bic)) {
            mt103Builder.orderingCustomerIdCode(bic);
        }
    }

    private void processBeneficiary(Beneficiary beneficiary, MT103Builder mt103Builder) {
        List<String> nameAndAddress = new ArrayList<>(); 
        
        addIfNotNull(beneficiary.getName(), nameAndAddress);
        
        if (beneficiary.getAddress() != null) {
            beneficiary.getAddress().forEach(address -> addIfNotNull(address, nameAndAddress));
        }
        
        mt103Builder.beneficiaryCustomerNameAndAddress(nameAndAddress);
        
    }

    private void addIfNotNull(String value, List<String> list) {
        if(isNotEmpty(value)) {
            list.add(value);
        }
    }

}

<%={{ }}=%>