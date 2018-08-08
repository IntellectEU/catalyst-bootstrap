{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import <%packageName%>.CamelHelloGrpc;
import <%packageName%>.CamelHelloReply;
import <%packageName%>.CamelHelloRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;

public class HelloCamelServer {

  private Server server;

  public void start() throws IOException {
    int port = 50051;
    server = ServerBuilder.forPort(port).addService(new HelloCamelImpl()).build().start();
    System.out.println("Server started. I'm listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        HelloCamelServer.this.stop();
      }
    });
  }

  public void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    final HelloCamelServer server = new HelloCamelServer();
    server.start();
    server.blockUntilShutdown();
  }

  static class HelloCamelImpl extends CamelHelloGrpc.CamelHelloImplBase {

    @Override
    public void sayHelloToCamel(CamelHelloRequest req,
        StreamObserver<CamelHelloReply> responseObserver) {
      CamelHelloReply reply = CamelHelloReply.newBuilder().setMessage("Hello " + req.getName())
          .build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
}
