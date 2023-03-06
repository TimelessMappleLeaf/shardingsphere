/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.test.e2e.engine.dml;

import org.apache.shardingsphere.test.e2e.cases.SQLCommandType;
import org.apache.shardingsphere.test.e2e.cases.SQLExecuteType;
import org.apache.shardingsphere.test.e2e.cases.value.SQLValue;
import org.apache.shardingsphere.test.e2e.env.runtime.IntegrationTestEnvironment;
import org.apache.shardingsphere.test.e2e.framework.param.array.E2ETestParameterFactory;
import org.apache.shardingsphere.test.e2e.framework.param.model.AssertionTestParameter;
import org.apache.shardingsphere.test.e2e.framework.runner.ParallelRunningStrategy;
import org.apache.shardingsphere.test.e2e.framework.runner.ParallelRunningStrategy.ParallelLevel;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ParallelRunningStrategy(ParallelLevel.SCENARIO)
public final class AdditionalDMLE2EIT extends BaseDMLE2EIT {
    
    public AdditionalDMLE2EIT(final AssertionTestParameter testParam) {
        super(testParam);
    }
    
    @Parameters(name = "{0}")
    public static Collection<AssertionTestParameter> getTestParameters() {
        if (IntegrationTestEnvironment.getInstance().isRunAdditionalTestCases()) {
            return E2ETestParameterFactory.getAssertionTestParameters(SQLCommandType.DML);
        }
        return Collections.emptyList();
    }
    
    @Test
    public void executeUpdateWithAutoGeneratedKeys() throws SQLException, ParseException {
        if (isPostgreSQLOrOpenGauss(getDatabaseType().getType())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType()
                    ? executeUpdateForStatementWithAutoGeneratedKeys(connection)
                    : executeUpdateForPreparedStatementWithAutoGeneratedKeys(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private boolean isPostgreSQLOrOpenGauss(final String databaseType) {
        return "PostgreSQL".equals(databaseType) || "openGauss".equals(databaseType);
    }
    
    private int executeUpdateForStatementWithAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate(String.format(getSQL(), getAssertion().getSQLValues().toArray()), Statement.RETURN_GENERATED_KEYS);
            assertGeneratedKeys(statement.getGeneratedKeys());
            return result;
        }
    }
    
    private int executeUpdateForPreparedStatementWithAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), Statement.RETURN_GENERATED_KEYS)) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            int result = preparedStatement.executeUpdate();
            assertGeneratedKeys(preparedStatement.getGeneratedKeys());
            return result;
        }
    }
    
    @Test
    @Ignore
    // TODO support execute update with column indexes in #23626
    public void assertExecuteUpdateWithColumnIndexes() throws SQLException, ParseException {
        if (isPostgreSQLOrOpenGauss(getDatabaseType().getType())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType() ? executeUpdateForStatementWithColumnIndexes(connection) : executeUpdateForPreparedStatementWithColumnIndexes(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeUpdateForStatementWithColumnIndexes(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(String.format(getSQL(), getAssertion().getSQLValues().toArray()), new int[]{1});
        }
    }
    
    private int executeUpdateForPreparedStatementWithColumnIndexes(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), new int[]{1})) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            return preparedStatement.executeUpdate();
        }
    }
    
    @Test
    @Ignore
    // TODO support execute update with column names in #23626
    public void assertExecuteUpdateWithColumnNames() throws SQLException, ParseException {
        if (isPostgreSQLOrOpenGauss(getDatabaseType().getType())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType() ? executeUpdateForStatementWithColumnNames(connection) : executeUpdateForPreparedStatementWithColumnNames(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeUpdateForStatementWithColumnNames(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(String.format(getSQL(), getAssertion().getSQLValues().toArray()));
        }
    }
    
    private int executeUpdateForPreparedStatementWithColumnNames(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), new String[]{"TODO"})) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            return preparedStatement.executeUpdate();
        }
    }
    
    @Test
    public void assertExecuteWithoutAutoGeneratedKeys() throws SQLException, ParseException {
        if (isPostgreSQLOrOpenGauss(getDatabaseType().getType())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType()
                    ? executeForStatementWithoutAutoGeneratedKeys(connection)
                    : executeForPreparedStatementWithoutAutoGeneratedKeys(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeForStatementWithoutAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(String.format(getSQL(), getAssertion().getSQLValues().toArray()), Statement.NO_GENERATED_KEYS), "Not a DML statement.");
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithoutAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), Statement.NO_GENERATED_KEYS)) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            return preparedStatement.getUpdateCount();
        }
    }
    
    @Test
    public void assertExecuteWithAutoGeneratedKeys() throws SQLException, ParseException {
        if (isPostgreSQLOrOpenGauss(getDatabaseType().getType())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType() ? executeForStatementWithAutoGeneratedKeys(connection) : executeForPreparedStatementWithAutoGeneratedKeys(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeForStatementWithAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(String.format(getSQL(), getAssertion().getSQLValues().toArray()), Statement.RETURN_GENERATED_KEYS), "Not a DML statement.");
            assertGeneratedKeys(statement.getGeneratedKeys());
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithAutoGeneratedKeys(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), Statement.RETURN_GENERATED_KEYS)) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            assertGeneratedKeys(preparedStatement.getGeneratedKeys());
            return preparedStatement.getUpdateCount();
        }
    }
    
    @Test
    @Ignore
    // TODO support execute with column indexes in #23626
    public void assertExecuteWithColumnIndexes() throws SQLException, ParseException {
        if (isPostgreSQLOrOpenGauss(getDatabaseType().getType())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType() ? executeForStatementWithColumnIndexes(connection) : executeForPreparedStatementWithColumnIndexes(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeForStatementWithColumnIndexes(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(String.format(getSQL(), getAssertion().getSQLValues().toArray()), new int[]{1}), "Not a DML statement.");
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithColumnIndexes(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), new int[]{1})) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            return preparedStatement.getUpdateCount();
        }
    }
    
    @Test
    @Ignore
    // TODO support execute with column names in #23626
    public void assertExecuteWithColumnNames() throws SQLException, ParseException {
        if (isPostgreSQLOrOpenGauss(getDatabaseType().getType())) {
            return;
        }
        int actualUpdateCount;
        try (Connection connection = getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == getSqlExecuteType() ? executeForStatementWithColumnNames(connection) : executeForPreparedStatementWithColumnNames(connection);
        }
        assertDataSet(actualUpdateCount);
    }
    
    private int executeForStatementWithColumnNames(final Connection connection) throws SQLException, ParseException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(String.format(getSQL(), getAssertion().getSQLValues().toArray()), new String[]{"TODO"}), "Not a DML statement.");
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithColumnNames(final Connection connection) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSQL(), new String[]{"TODO"})) {
            for (SQLValue each : getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            return preparedStatement.getUpdateCount();
        }
    }
}
