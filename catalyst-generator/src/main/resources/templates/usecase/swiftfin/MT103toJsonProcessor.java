{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import com.intellecteu.catalyst.swift.converter.CatalystMtXmlConverter;
import com.intellecteu.catalyst.swift.converter.MTMessage;
import com.intellecteu.catalyst.swift.converter.MTUtils;
import <%fullPackageName%>.model.Fin103Dto;
import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swift.xsd.fin_103.*;
import swift.xsd.mtmsg.Block1FinType;
import swift.xsd.mtmsg.MessageInputReferenceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class MT103toJsonProcessor {
    private final CatalystMtXmlConverter converter;

    @Autowired
    public MT103toJsonProcessor(CatalystMtXmlConverter converter) {
        this.converter = converter;
    }
    
    @Handler
    public Fin103Dto mt103ToJson(@Body String strMt103) throws Exception {
        MTMessage message = converter.parseMt(strMt103);
        Fin103Dto.Fin103DtoBuilder builder = Fin103Dto.builder();

        Document mt103Document = (Document) message.getBlock4();
        MT103Type mt103 = mt103Document.getMT103();

        builder.sendersReference(mt103.getF20A().getF20());

        Fin103Dto.OperationalCode operationalCode = Fin103Dto.OperationalCode
                .valueOf(mt103.getF23A1().getF23B().value());
        builder.bankOperationalCode(operationalCode);

        F32AType f32A = mt103.getF32A().getF32A();
        LocalDate valueDate = LocalDate.parse(f32A.getDate(), MTUtils.FIN_FORMATTER);
        BigDecimal amount = MTUtils.parseAmount(f32A.getAmount());
        builder
                .valueDate(valueDate)
                .amount(amount)
                .currency(f32A.getCurrency());

        F50A2Type f50A = mt103.getF50A().getF50A();
        builder.orderingCustomerBIC(f50A.getIdentifierCode());
        builder.orderingCustomerAccount(f50A.getAccount());

        F593Type f59 = mt103.getF59A().getF59();
        List<String> nameAndAddress = f59.getNameAndAddress().getLine();
        builder
                .beneficiaryName(nameAndAddress.get(0))
                .beneficiaryAddressLine1(nameAndAddress.get(1))
                .beneficiaryAddressLine2(nameAndAddress.get(2));
        
        String[] split = nameAndAddress.get(3).split(" ");
        builder
                .beneficiaryCity(split[0])
                .beneficiaryCountry(split[1]);

        Fin103Dto.Charges charges = Fin103Dto.Charges.fromValue(mt103.getF71A1().getF71A().value());
        builder.detailsOfCharges(charges);

        Block1FinType block1 = message.getBlock123().getBlock1();
        builder
                .receiverBIC(block1.getLogicalTerminalAddress().substring(0,8))
                .receiverLT(block1.getLogicalTerminalAddress().substring(8,9));

        MessageInputReferenceType mir = message.getBlock123().getBlock2().getMessageInputReference();
        builder
                .senderBIC(mir.getLTIdentifier().substring(0,8))
                .senderLT(mir.getLTIdentifier().substring(8,9));

        return builder.build();
    }
}

<%={{ }}=%>