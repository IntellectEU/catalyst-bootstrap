{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

public class PaymentResponse {
  private int id;
  private String status;

  public PaymentResponse() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "PaymentResponse{" +
        "id=" + id +
        ", status='" + status + '\'' +
        '}';
  }
}

<%={{ }}=%>