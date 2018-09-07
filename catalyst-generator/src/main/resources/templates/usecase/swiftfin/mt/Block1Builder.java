{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import swift.xsd.mtmsg.Block1FinType;
import swift.xsd.mtmsg.Code1Ea15Type;

import static com.intellecteu.catalyst.swift.converter.MTUtils.joinBic11WithLt;

public class Block1Builder {

    private final Block1FinType block1;

    private Block1Builder() {
        block1 = new Block1FinType();
    }
    
    public static Block1Builder forBIC(String bic) {
        Block1Builder builder = new Block1Builder();
        
        builder.block1.setApplicationIdentifier(Code1Ea15Type.F);
        builder.block1.setServiceIdentifier("01");
        builder.block1.setLogicalTerminalAddress(joinBic11WithLt(bic));
        builder.block1.setSessionNumber("0000");
        builder.block1.setSequenceNumber("000000");
        
        return builder;
    }
    
    public Block1FinType build() {
        return block1;
    }

}

<%={{ }}=%>