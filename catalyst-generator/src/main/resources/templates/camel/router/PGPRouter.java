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

  @Value("${catalyst.pgp.privateKeyFile}")
  private String privateKeyFile;

  @Value("${catalyst.pgp.privateKeyUserId}")
  private String privateKeyUserId;

  @Value("${catalyst.pgp.privateKeyPassword}")
  private String privateKeyPassword;

  @Value("${catalyst.pgp.publicKeyFile}")
  private String publicKeyFile;

  @Value("${catalyst.pgp.publicKeyUserId}")
  private String publicKeyUserId;


  @Override
  public void configure() {
    from("timer:name?period=2000")
        .setBody(simple("${bean:java.lang.System?method=currentTimeMillis}", String.class))
        .log("Original message: ${body}")
        .to("direct:encrypt");

    from("direct:encrypt")
        // Encrypt the message with public key
        .marshal().pgp(publicKeyFile, publicKeyUserId)
        .log("Encrypted message: ${bodyAs(java.lang.String)}")
        .to("direct:decrypt");

    from("direct:decrypt")
        // Decrypt using private key and password (passphrase)
        .unmarshal().pgp(privateKeyFile, privateKeyUserId, privateKeyPassword)
        .log("Decrypted message: ${body}");
  }
}

<%={{ }}=%>