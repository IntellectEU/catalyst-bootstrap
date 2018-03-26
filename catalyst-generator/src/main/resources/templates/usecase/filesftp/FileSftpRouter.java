{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel routes for file/sftp connectors
 */
@Component
public class FileSftpRouter extends RouteBuilder {

  private static final String SAVE_FILE_ROUTE = "SaveFileRoute";
  private static final String READ_FILE_ROUTE = "ReadFileRoute";
  private static final String SAVE_FILE = "SaveFile";
  private static final String SAVE_FILE_SFTP = "SaveFileSftp";

  @Override
  public void configure() {

//    Endpoint configuration for file and sftp
    Endpoint sftpEndpointIn = getContext()
        .getEndpoint(
            "sftp://{{catalyst.filesftp.sftp.host}}:{{catalyst.filesftp.sftp.port}}/{{catalyst.filesftp.sftp.dir.in}}"
                + "?pollStrategy=#ftpPollStrategy"
                + "&throwExceptionOnConnectFailed={{catalyst.filesftp.sftp.throwExceptionOnConnectFailed}}"
                + "&binary={{catalyst.filesftp.sftp.binary}}"
                + "&passiveMode={{catalyst.filesftp.sftp.passiveMode}}"
                + "&readLock={{catalyst.filesftp.sftp.readLock}}"
                + "&delete={{catalyst.filesftp.sftp.delete}}"
                + "&readLockCheckInterval={{catalyst.filesftp.sftp.readLockCheckInterval}}"
                + "&readLockTimeout={{catalyst.filesftp.sftp.readLockTimeout}}"
                + "&include={{catalyst.filesftp.sftp.include}}"
                + "&exclude={{catalyst.filesftp.sftp.exclude}}"
                + "&delay={{catalyst.filesftp.sftp.delay}}"
                + "&localWorkDirectory={{catalyst.filesftp.sftp.localWorkDirectory}}"
                + "&maxMessagesPerPoll={{catalyst.filesftp.sftp.maxMessagesPerPoll}}"
                + "&knownHostsFile={{catalyst.filesftp.sftp.knownHostsFile}}"
                + "&privateKeyFile={{catalyst.filesftp.sftp.privateKeyFile}}"
                + "&privateKeyPassphrase=RAW({{catalyst.filesftp.sftp.privateKeyPassphrase}})"
                + "&consumer.bridgeErrorHandler={{catalyst.filesftp.sftp.consumer.bridgeErrorHandler}}"
                + "&readLockLoggingLevel={{catalyst.filesftp.sftp.readLockLoggingLevel}}");

    Endpoint sftpEndpointOut = getContext().getEndpoint(
        "sftp://{{catalyst.filesftp.sftp.host}}:{{catalyst.filesftp.sftp.port}}/{{catalyst.filesftp.sftp.dir.out}}"
            + "?binary={{catalyst.filesftp.sftp.binary}}"
            + "&passiveMode={{catalyst.filesftp.sftp.passiveMode}}"
            + "&throwExceptionOnConnectFailed={{catalyst.filesftp.sftp.throwExceptionOnConnectFailed}}"
            + "&username={{catalyst.filesftp.sftp.username}}"
            + "&password={{catalyst.filesftp.sftp.password}}"
            + "&preferredAuthentications={{catalyst.filesftp.sftp.preferredAuthentications}}"
            + "&knownHostsFile={{catalyst.filesftp.sftp.knownHostsFile}}"
            + "&privateKeyFile={{catalyst.filesftp.sftp.privateKeyFile}}"
            + "&privateKeyPassphrase=RAW({{catalyst.filesftp.sftp.privateKeyPassphrase}})");

    Endpoint fileEndpointIn = getContext().getEndpoint(
        "file://{{catalyst.filesftp.file.dir.in}}?move={{catalyst.filesftp.file.dir.done}}&moveFailed={{catalyst.filesftp.file.dir.failed}}");
    Endpoint fileEndpointOut = getContext()
        .getEndpoint("file://{{catalyst.filesftp.file.dir.out}}?fileName=${headers.CamelFileName}");

    onException(Exception.class)
        // Insert general error handling
        .log(LoggingLevel.ERROR,"Root Exception Handler - Caught unhandled exception ${exception.message}");

    onException(IllegalArgumentException.class)
        .handled(true) // Prevent Camel error handlers to process exception, as we handle it ourselves
        // Insert specific error handling
        .log("Specific exception handler")
        .to("log:error?showCaughtException=true&showStackTrace=true");

    // Consume files from fileEndpointIn
    // Save file into sftpEndpointOut using CamelFileName header (Exchange.FILE_NAME).
    from(fileEndpointIn)
        .id(READ_FILE_ROUTE)
        .autoStartup("{{catalyst.filesftp.fileReader.enabled}}")
        .log(
            "Received file [${headers.CamelFileName}] from [{{catalyst.filesftp.file.dir.in}}] directory.")
        .log(
            "Saving file into [{{catalyst.filesftp.sftp.dir.out}}] using name [${headers.CamelFileName}]...")
        .to(sftpEndpointOut).id(SAVE_FILE_SFTP);

    // Consume files from sftpEndpointIn.
    // Save file into fileEndpointOut using CamelFileName header (Exchange.FILE_NAME).
    from(sftpEndpointIn)
        .id(SAVE_FILE_ROUTE).autoStartup("{{catalyst.filesftp.sftpReader.enabled}}")
        .log(
            "Received file [${headers.CamelFileName}] from [{{catalyst.filesftp.sftp.dir.in}}] directory.")
        .log(
            "Saving file into [{{catalyst.filesftp.file.dir.out}}] using name [${headers.CamelFileName}]...")
        .to(fileEndpointOut).id(SAVE_FILE);

  }

}
<%={{ }}=%>
