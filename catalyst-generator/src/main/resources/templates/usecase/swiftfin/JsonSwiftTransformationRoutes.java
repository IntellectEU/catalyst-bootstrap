{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import <%fullPackageName%>.model.Fin103Dto;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonSwiftTransformationRoutes extends RouteBuilder {
    private final JsonToMT103Processor jsonToMT103Processor;
    private final MT103toJsonProcessor mt103toJsonProcessor;

    @Autowired
    public JsonSwiftTransformationRoutes(JsonToMT103Processor jsonToMT103Processor, MT103toJsonProcessor mt103toJsonProcessor) {
        this.jsonToMT103Processor = jsonToMT103Processor;
        this.mt103toJsonProcessor = mt103toJsonProcessor;
    }

    @Override
    public void configure() {
        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat(Fin103Dto.class);
        jacksonDataFormat.setPrettyPrint(true);
        jacksonDataFormat.addModule(new JavaTimeModule());
        jacksonDataFormat.disableFeature(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        from("direct:transformJsonToMT103")
                .log("Marshalling JSON string to Object...")
                .unmarshal(jacksonDataFormat)
                
                .log("Transforming JSON object to MT103...")
                .bean(jsonToMT103Processor)
                
                .log("MT103 created.")
                .to("mock:jsonToMT103Result");
        
        from("direct:transformMT103toJson")
                .log("Converting MT103 RJE to JSON Object...")
                .bean(mt103toJsonProcessor)
                
                .log("Marshalling JSON object to String...")
                .marshal(jacksonDataFormat)
                
                .log("JSON String created")
                .to("mock:mt103toJsonResult");
    }
}

<%={{ }}=%>