{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import com.intellecteu.catalyst.swift.converter.CatalystMtXmlConverter;
import com.intellecteu.catalyst.swift.converter.MTMessage;
import com.intellecteu.catalyst.swift.converter.MTUtils;
import <%fullPackageName%>.model.Fin103Dto;
import <%fullPackageName%>.mt.Block1Builder;
import <%fullPackageName%>.mt.Block2Builder;
import <%fullPackageName%>.mt.MT103Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swift.xsd.fin_103.Document;
import swift.xsd.fin_103.MT103Type;
import swift.xsd.mtmsg.FinMessageType;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Component
public class JsonToMT103Processor {
    private final CatalystMtXmlConverter converter;

    @Autowired
    public JsonToMT103Processor(CatalystMtXmlConverter converter) {
        this.converter = converter;
    }

    @Handler
    public String jsonToMT103(@Body Fin103Dto jsonData) throws Exception {
        log.info("Creating MT103 message from data:\n{}", jsonData);

        String amount = MTUtils.parseAmount(jsonData.getAmount());

        // Create MT103 Builder
        MT103Builder mt103Builder = MT103Builder.core()
                // F20
                .sendersReference(jsonData.getSendersReference())
                // F23B
                .bankOperationCode(jsonData.getBankOperationalCode().value())
                // F32A
                .dateCurrencyAmount(jsonData.getValueDate(), jsonData.getCurrency(), amount)
                // F71A
                .charges(jsonData.getDetailsOfCharges().value());

        // Ordering Customer
        String bic = jsonData.getOrderingCustomerBIC();
        String account = jsonData.getOrderingCustomerAccount();

        if ( isNotEmpty(bic) && isNotEmpty(account) ) {
            mt103Builder.orderingCustomerIdCodeAndAccount(bic, account);
        } else if (isNotEmpty(bic)) {
            mt103Builder.orderingCustomerIdCode(bic);
        }
        
        // Beneficiary
        List<String> nameAndAddress = new ArrayList<>();
        nameAndAddress.add(jsonData.getBeneficiaryName());
        nameAndAddress.add(jsonData.getBeneficiaryAddressLine1());
        nameAndAddress.add(jsonData.getBeneficiaryAddressLine2());
        nameAndAddress.add(jsonData.getBeneficiaryCity() + " " + jsonData.getBeneficiaryCountry());

        mt103Builder.beneficiaryCustomerNameAndAddress(nameAndAddress);

        // Build MT103 body (Block 4)
        MT103Type mt103 = mt103Builder.build();

        // Create JAXB representation
        Document mt103Document = new Document();
        mt103Document.setMT103(mt103);

        // Create Blocks 1 and 2
        FinMessageType finMessageType = new FinMessageType();
        finMessageType.setBlock1(Block1Builder.forBIC(jsonData.getSenderBIC()).build());
        finMessageType.setBlock2(Block2Builder.input(jsonData.getReceiverBIC()).build());

        // Create MTMessage and send it to exchange body
        MTMessage mtMessage = new MTMessage(finMessageType);
        mtMessage.setBlock4(mt103Document);

        // Convert to RJE
        return converter.printMt(mtMessage);
    }
}

<%={{ }}=%>