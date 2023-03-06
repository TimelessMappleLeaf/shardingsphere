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

package org.apache.shardingsphere.driver.state.circuit.connection;

import org.apache.shardingsphere.driver.state.circuit.metadata.CircuitBreakerDatabaseMetaData;
import org.apache.shardingsphere.driver.state.circuit.statement.CircuitBreakerPreparedStatement;
import org.apache.shardingsphere.driver.state.circuit.statement.CircuitBreakerStatement;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;

public final class CircuitBreakerConnectionTest {
    
    private final CircuitBreakerConnection connection = new CircuitBreakerConnection();
    
    @Test
    public void assertGetMetaData() {
        assertThat(connection.getMetaData(), instanceOf(CircuitBreakerDatabaseMetaData.class));
    }
    
    @Test
    public void setReadOnly() {
        connection.setReadOnly(true);
        assertFalse(connection.isReadOnly());
    }
    
    @Test
    public void assertIsReadOnly() {
        assertFalse(connection.isReadOnly());
    }
    
    @Test
    public void assertSetTransactionIsolation() {
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        assertThat(connection.getTransactionIsolation(), is(Connection.TRANSACTION_NONE));
    }
    
    @Test
    public void assertGetTransactionIsolation() {
        assertThat(connection.getTransactionIsolation(), is(Connection.TRANSACTION_NONE));
    }
    
    @Test
    public void assertGetWarnings() {
        assertNull(connection.getWarnings());
    }
    
    @Test
    public void assertClearWarnings() {
        connection.clearWarnings();
    }
    
    @Test
    public void assertSetAutoCommit() {
        connection.setAutoCommit(true);
        assertFalse(connection.getAutoCommit());
    }
    
    @Test
    public void assertGetAutoCommit() {
        assertFalse(connection.getAutoCommit());
    }
    
    @Test
    public void assertCommit() {
        connection.commit();
    }
    
    @Test
    public void assertRollback() {
        connection.rollback();
    }
    
    @Test
    public void assertSetHoldability() {
        connection.setHoldability(-1);
        assertThat(connection.getHoldability(), is(0));
    }
    
    @Test
    public void assertGetHoldability() {
        assertThat(connection.getHoldability(), is(0));
    }
    
    @Test
    public void assertPrepareStatement() {
        String sql = "SELECT 1";
        assertThat(connection.prepareStatement(sql), instanceOf(CircuitBreakerPreparedStatement.class));
        assertThat(connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY), instanceOf(CircuitBreakerPreparedStatement.class));
        assertThat(connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT), instanceOf(CircuitBreakerPreparedStatement.class));
        assertThat(connection.prepareStatement(sql, Statement.NO_GENERATED_KEYS), instanceOf(CircuitBreakerPreparedStatement.class));
        assertThat(connection.prepareStatement(sql, new int[]{0}), instanceOf(CircuitBreakerPreparedStatement.class));
        assertThat(connection.prepareStatement(sql, new String[]{""}), instanceOf(CircuitBreakerPreparedStatement.class));
    }
    
    @Test
    public void assertCreateStatement() {
        assertThat(connection.createStatement(), instanceOf(CircuitBreakerStatement.class));
        assertThat(connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY), instanceOf(CircuitBreakerStatement.class));
        assertThat(connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT), instanceOf(CircuitBreakerStatement.class));
    }
    
    @Test
    public void assertClose() {
        connection.close();
    }
    
    @Test
    public void assertIsClosed() {
        assertFalse(connection.isClosed());
    }
}
