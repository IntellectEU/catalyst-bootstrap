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

package <%packageName%>;

import java.util.Objects;

public class CarData {

  private int deviceId;
  private int carId;
  private double miles;


  public CarData() {
  }

  public CarData(int DeviceId, double miles) {
    this.deviceId = DeviceId;
    this.miles = miles;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }

  public double getMiles() {
    return miles;
  }

  public void setMiles(double miles) {
    this.miles = miles;
  }

  public int getCarId() {
    return carId;
  }

  public void setCarId(int carId) {
    this.carId = carId;
  }

  @Override
  public String toString() {
    return "CarData{" +
        "deviceId=" + deviceId +
        ", carId=" + carId +
        ", miles=" + miles +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CarData)) return false;
    CarData carData = (CarData) o;
    return getDeviceId() == carData.getDeviceId() &&
            getCarId() == carData.getCarId() &&
            Double.compare(carData.getMiles(), getMiles()) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getDeviceId(), getCarId(), getMiles());
  }
}
