{{=<% %>=}}

<%license%>

package <%packageName%>;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Sample Camel route to demonstrate encryption and decryption using PGP
 */
@Component
public class PGPRouter extends RouteBuilder {

  @Override
  public void configure() {
    from("timer:name?period=2000")
        .setBody(simple("${bean:java.lang.System?method=currentTimeMillis}", String.class))
        .log("Original message: ${body}")
        .to("direct:encrypt");

    from("direct:encrypt")
        // Encrypt the message with public key
        .marshal().pgp("{{catalyst.pgp.publicKeyFile}}",
        "{{catalyst.pgp.publicKeyUserId}}")
        .log("Encrypted message: ${bodyAs(java.lang.String)}")
        .to("direct:decrypt");

    from("direct:decrypt")
        // Decrypt using private key and password (passphrase)
        .unmarshal().pgp("{{catalyst.pgp.privateKeyFile}}",
        "{{catalyst.pgp.privateKeyUserId}}",
        "{{catalyst.pgp.privateKeyPassword}}")
        .log("Decrypted message: ${body}");
  }
}

<%={{ }}=%>