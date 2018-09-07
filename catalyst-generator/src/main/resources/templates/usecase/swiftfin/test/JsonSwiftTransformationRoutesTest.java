{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JsonSwiftTransformationRoutesTest {
    private final static String CRLF = "\r\n";
    private final static String MT103_TO_SWIFT = "{1:F01SPXBUAU0XXXX0000000000}{2:I103CITYUS30XXXXN}{4:" + CRLF +
            ":20:TestJson" + CRLF +
            ":23B:CRTS" + CRLF +
            ":32A:180905USD100500,99" + CRLF +
            ":50A:/1234567890" + CRLF +
            "SPXBUAUK" + CRLF +
            ":59:Vanilla Guy" + CRLF +
            "123 Main Ave" + CRLF +
            "Office 777" + CRLF +
            "Kyiv Ukraine" + CRLF +
            ":71A:SHA" + CRLF +
            "-}";

    private final static String MT103_FROM_SWIFT = "{1:F01SPXBUAU0XXXX0000000000}{2:O1031636180819CITYUS30AXXX00010000061808191637N}{4:" + CRLF +
            ":20:TestJson" + CRLF +
            ":23B:CRTS" + CRLF +
            ":32A:180905USD100500,99" + CRLF +
            ":50A:/1234567890" + CRLF +
            "SPXBUAUK" + CRLF +
            ":59:Vanilla Guy" + CRLF +
            "123 Main Ave" + CRLF +
            "Office 777" + CRLF +
            "Kyiv Ukraine" + CRLF +
            ":71A:SHA" + CRLF +
            "-}";
    
    private final static String JSON_TO_SWIFT = "{\n" +
            "  \"senderBIC\" : \"SPXBUAU0\",\n" +
            "  \"senderLT\" : \"X\",\n" +
            "  \"receiverBIC\" : \"CITYUS30\",\n" +
            "  \"receiverLT\" : \"X\",\n" +
            "  \"sendersReference\" : \"TestJson\",\n" +
            "  \"bankOperationalCode\" : \"CRTS\",\n" +
            "  \"valueDate\" : \"2018/09/05\",\n" +
            "  \"currency\": \"USD\",\n" +
            "  \"amount\" : 100500.99,\n" +
            "  \"orderingCustomerBIC\" : \"SPXBUAUK\",\n" +
            "  \"orderingCustomerAccount\" : \"1234567890\",\n" +
            "  \"beneficiaryName\" : \"Vanilla Guy\",\n" +
            "  \"beneficiaryAddressLine1\" : \"123 Main Ave\",\n" +
            "  \"beneficiaryAddressLine2\" : \"Office 777\",\n" +
            "  \"beneficiaryCity\" : \"Kyiv\",\n" +
            "  \"beneficiaryCountry\" : \"Ukraine\",\n" +
            "  \"detailsOfCharges\" : \"SHA\"\n" +
            "}";
    
    private final static String JSON_FROM_SWIFT = "{\n" +
            "  \"senderBIC\" : \"CITYUS30\",\n" +
            "  \"senderLT\" : \"A\",\n" +
            "  \"receiverBIC\" : \"SPXBUAU0\",\n" +
            "  \"receiverLT\" : \"X\",\n" +
            "  \"sendersReference\" : \"TestJson\",\n" +
            "  \"bankOperationalCode\" : \"CRTS\",\n" +
            "  \"valueDate\" : \"2018/09/05\",\n" +
            "  \"currency\" : \"USD\",\n" +
            "  \"amount\" : 100500.99,\n" +
            "  \"orderingCustomerBIC\" : \"SPXBUAUK\",\n" +
            "  \"orderingCustomerAccount\" : \"1234567890\",\n" +
            "  \"beneficiaryName\" : \"Vanilla Guy\",\n" +
            "  \"beneficiaryAddressLine1\" : \"123 Main Ave\",\n" +
            "  \"beneficiaryAddressLine2\" : \"Office 777\",\n" +
            "  \"beneficiaryCity\" : \"Kyiv\",\n" +
            "  \"beneficiaryCountry\" : \"Ukraine\",\n" +
            "  \"detailsOfCharges\" : \"SHA\"\n" +
            "}";
    
    @Produce(uri = "direct:transformJsonToMT103")
    private ProducerTemplate transformJsonToMT103;
    
    @EndpointInject(uri = "mock:jsonToMT103Result")
    private MockEndpoint jsonToMT103Result;
        
    @Produce(uri = "direct:transformMT103toJson")
    private ProducerTemplate transformMT103toJson;
    
    @EndpointInject(uri = "mock:mt103toJsonResult")
    private MockEndpoint mt103toJsonResult;
        
    
    @Test
    public void givenValidJson_thenMT103Created() throws InterruptedException {
        jsonToMT103Result.expectedBodiesReceived(MT103_TO_SWIFT);
        
        transformJsonToMT103.sendBody(JSON_TO_SWIFT);
        
        jsonToMT103Result.assertIsSatisfied();
    }
    
    @Test
    public void givenValidMT103_thenJsonCreated() throws InterruptedException {
        mt103toJsonResult.expectedBodiesReceived(JSON_FROM_SWIFT);
        
        transformMT103toJson.sendBody(MT103_FROM_SWIFT);
        
        mt103toJsonResult.assertIsSatisfied();
    }

}

<%={{ }}=%>