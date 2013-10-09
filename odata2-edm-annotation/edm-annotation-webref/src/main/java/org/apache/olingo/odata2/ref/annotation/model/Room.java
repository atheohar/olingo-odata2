/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.olingo.odata2.ref.annotation.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.NavigationEnd;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;

/**
 *
 */
@EdmEntityType(name = "Room", namespace = ModelSharedConstants.NAMESPACE_1)
@EdmEntitySet(name = "Rooms", container = ModelSharedConstants.CONTAINER_1)
public class Room extends RefBase {

  @EdmProperty
  private Integer seats;
  @EdmProperty
  private Integer version;
  @EdmNavigationProperty(name="nr_Building", relationship="BuildingRooms",
          from = @NavigationEnd(role="r_Room", multiplicity = EdmMultiplicity.MANY))
  private Building building;
  @EdmNavigationProperty(name="nr_Employees", relationship="RoomEmployees", 
          from = @NavigationEnd(role = "r_Room", type = "Room", multiplicity = EdmMultiplicity.ONE),
          to = @NavigationEnd(role = "r_Employees", type = "Employee", multiplicity = EdmMultiplicity.MANY))
  private List<Employee> employees = new ArrayList<Employee>();

  public Room(final int id, final String name) {
    super(id, name);
  }

  public void setSeats(final int seats) {
    this.seats = seats;
  }

  public int getSeats() {
    return seats;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public int getVersion() {
    return version;
  }

  public void setBuilding(final Building building) {
    this.building = building;
  }

  public Building getBuilding() {
    return building;
  }

  public List<Employee> getEmployees() {
    return employees;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj
            || obj != null && getClass() == obj.getClass() && id == ((Room) obj).id;
  }

  @Override
  public String toString() {
    return "{\"Id\":\"" + id + "\",\"Name\":\"" + name + "\",\"Seats\":" + seats + ",\"Version\":" + version + "}";
  }
}
