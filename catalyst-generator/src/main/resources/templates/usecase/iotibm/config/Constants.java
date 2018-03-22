{{=<% %>=}}

<%license%>

package <%packageName%>;

public interface Constants {

  String REST_API_ADDRESS = "REST_API_ADDRESS";
  String HOST = System.getenv(REST_API_ADDRESS);
  String AUTHORIZATION_HEADER = "Authorization";
  String BEAR_HEADER = "Bearer";
  String OWNER = "a";
  String PEER = "peer0";
  String TOKEN = "token";
  String CHANNEL = "public";
  String CHAINCODE = "insurance";
  String GET_FUNCTION = "getNew";
  String PUT_FUNCTION = "put";
  String SET_FUNCTION = "setStatus";
  String PAID_STATUS = "paid";
  String REJECTED_STATUS = "rejected";
  String PENDING_STATUS = "pending";
}
