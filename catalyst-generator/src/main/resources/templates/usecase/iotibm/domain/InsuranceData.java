/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

{{=<% %>=}}

<%license%>

package <%fullPackageName%>;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuranceData {
  @JsonProperty("PaymentId")
  private int paymentId;
  @JsonProperty("CarId")
  private int carId;
  @JsonProperty("UserId")
  private int userId;
  @JsonProperty("DeltaMiles")
  private double deltaMiles;
  @JsonProperty("Premium")
  private double premium;

  public InsuranceData() {
  }


  public int getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(int paymentId) {
    this.paymentId = paymentId;
  }

  public int getCarId() {
    return carId;
  }

  public void setCarId(int carId) {
    this.carId = carId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public double getDeltaMiles() {
    return deltaMiles;
  }

  public void setDeltaMiles(double deltaMiles) {
    this.deltaMiles = deltaMiles;
  }

  public double getPremium() {
    return premium;
  }

  public void setPremium(double premium) {
    this.premium = premium;
  }


  @Override
  public String toString() {
    return "InsuranceData{" +
        "carId=" + carId +
        ", userId=" + userId +
        ", deltaMiles=" + deltaMiles +
        ", premium=" + premium +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof InsuranceData)) return false;
    InsuranceData that = (InsuranceData) o;
    return getPaymentId() == that.getPaymentId() &&
            getCarId() == that.getCarId() &&
            getUserId() == that.getUserId() &&
            Double.compare(that.getDeltaMiles(), getDeltaMiles()) == 0 &&
            Double.compare(that.getPremium(), getPremium()) == 0;
  }

  @Override
  public int hashCode() {

    return Objects.hash(getPaymentId(), getCarId(), getUserId(), getDeltaMiles(), getPremium());
  }
}

<%={{ }}=%>