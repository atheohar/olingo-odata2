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
package org.apache.olingo.odata2.jpa.processor.core.jpql;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmMapping;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.provider.Mapping;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.uri.NavigationSegment;
import org.apache.olingo.odata2.jpa.processor.api.access.JPAJoinClause;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPAModelException;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLContext;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLContextType;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLJoinContextView;
import org.apache.olingo.odata2.jpa.processor.core.ODataExpressionParser;

public class JPQLJoinSelectContext extends JPQLSelectContext implements JPQLJoinContextView {

  private List<JPAJoinClause> jpaJoinClauses = null;

  protected void setJPAOuterJoinClause(final List<JPAJoinClause> jpaOuterJoinClauses) {
    jpaJoinClauses = jpaOuterJoinClauses;
  }

  public JPQLJoinSelectContext(final boolean isCountOnly) {
    super(isCountOnly);
  }

  public class JPQLJoinContextBuilder extends JPQLSelectContextBuilder {

    protected int relationShipAliasCounter = 0;

    @Override
    public JPQLContext build() throws ODataJPAModelException, ODataJPARuntimeException {
      try {

        if (JPQLJoinSelectContext.this.isCountOnly) {
          setType(JPQLContextType.JOIN_COUNT);
        } else {
          setType(JPQLContextType.JOIN);
        }

        if (withPaging) {
          isPagingRequested(withPaging);
        }

        setJPAOuterJoinClause(generateJoinClauses());

        if (!jpaJoinClauses.isEmpty()) {
          JPAJoinClause joinClause = jpaJoinClauses.get(jpaJoinClauses.size() - 1);
          setJPAEntityName(joinClause.getEntityName());
          setJPAEntityAlias(joinClause.getEntityRelationShipAlias());
        }

        setOrderByCollection(generateOrderByFileds());

        setSelectExpression(generateSelectExpression());

        setWhereExpression(generateWhereExpression());

      } catch (ODataException e) {
        throw ODataJPARuntimeException.throwException(ODataJPARuntimeException.INNER_EXCEPTION, e);
      }

      return JPQLJoinSelectContext.this;
    }

    protected List<JPAJoinClause> generateJoinClauses() throws ODataJPARuntimeException, EdmException {

      List<JPAJoinClause> jpaOuterJoinClauses = new ArrayList<JPAJoinClause>();
      JPAJoinClause jpaOuterJoinClause = null;
      String joinCondition = null;
      String entityAlias = generateJPAEntityAlias();
      joinCondition = ODataExpressionParser.parseKeyPredicates(entitySetView.getKeyPredicates(), entityAlias);

      EdmEntityType entityType = entitySetView.getStartEntitySet().getEntityType();
      Mapping mapping = (Mapping) entityType.getMapping();
      String entityTypeName = null;
      if (mapping != null) {
        entityTypeName = mapping.getInternalName();
      } else {
        entityTypeName = entityType.getName();
      }

      jpaOuterJoinClause =
          new JPAJoinClause(entityTypeName, entityAlias, null, null, joinCondition, JPAJoinClause.JOIN.INNER);

      jpaOuterJoinClauses.add(jpaOuterJoinClause);

      for (NavigationSegment navigationSegment : entitySetView.getNavigationSegments()) {

        EdmNavigationProperty navigationProperty = navigationSegment.getNavigationProperty();

        String relationShipAlias = generateRelationShipAlias();

        joinCondition =
            ODataExpressionParser.parseKeyPredicates(navigationSegment.getKeyPredicates(), relationShipAlias);

        jpaOuterJoinClause =
            new JPAJoinClause(getFromEntityName(navigationProperty), entityAlias,
                getRelationShipName(navigationProperty), relationShipAlias, joinCondition, JPAJoinClause.JOIN.INNER);

        jpaOuterJoinClauses.add(jpaOuterJoinClause);

      }

      return jpaOuterJoinClauses;
    }

    private String getFromEntityName(final EdmNavigationProperty navigationProperty) throws EdmException {

      String fromRole = navigationProperty.getFromRole();

      EdmEntityType toEntityType = navigationProperty.getRelationship().getEnd(fromRole).getEntityType();

      EdmMapping mapping = toEntityType.getMapping();

      String entityName = null;
      if (mapping != null) {
        entityName = mapping.getInternalName();
      } else {
        entityName = toEntityType.getName();
      }

      return entityName;

    }

    private String getRelationShipName(final EdmNavigationProperty navigationProperty) throws EdmException {

      EdmMapping mapping = navigationProperty.getMapping();

      String relationShipName = null;
      if (mapping != null) {
        relationShipName = mapping.getInternalName();
      } else {
        relationShipName = navigationProperty.getName();
      }

      return relationShipName;
    }

    private String generateRelationShipAlias() {
      return new String("R" + ++relationShipAliasCounter);
    }
  }

  @Override
  public List<JPAJoinClause> getJPAJoinClauses() {
    return jpaJoinClauses;
  }

}
