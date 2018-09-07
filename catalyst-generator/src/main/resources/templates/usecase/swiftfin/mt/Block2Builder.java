{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import com.intellecteu.catalyst.swift.converter.MTUtils;
import swift.xsd.mtmsg.Block2FinType;
import swift.xsd.mtmsg.Code1Ea14Type;
import swift.xsd.mtmsg.Code1Ea16Type;

public class Block2Builder {

    private final Block2FinType block2;

    private Block2Builder() {
        block2 = new Block2FinType();
    }

    public static Block2Builder input(String bic) {
        Block2Builder builder = new Block2Builder();
        
        builder.block2.setInputIdentifier(Code1Ea16Type.I);
        builder.block2.setInMessageType("103");
        builder.block2.setDestinationAddress(MTUtils.joinBic11WithLt(bic));
        builder.block2.setInMessagePriority(Code1Ea14Type.N);
        
        return builder;
    }

    public Block2FinType build() {
        return block2;
    }
}

<%={{ }}=%>