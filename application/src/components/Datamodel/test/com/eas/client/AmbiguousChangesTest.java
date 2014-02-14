/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.DelegatingFlowProvider;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.BaseTest;
import com.eas.client.queries.Query;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class AmbiguousChangesTest extends BaseTest {

    public static final String AMBIGUOUS_QUERY_ID = "134564170799279";
    public static final String AMBIGUOUS_SEMI_WRITABLE_QUERY_ID = "test.ambigous.changes.mutatable";//@mutatable Asset_Kinds, AssET_TYPES
    public static final String COMMAND_QUERY_ID = "134570075809763";
    public static final BigDecimal NEW_RECORD_ID = BigDecimal.valueOf(4125L);
    public static final String NEW_RECORD_NAME_G = "test gname";
    public static final String NEW_RECORD_NAME_T = "test tname";
    public static final String NEW_RECORD_NAME_K = "test kname";

    @Test
    public void threeTablesTest() throws Exception {
        try (DatabasesClientWithResource resource = BaseTest.initDevelopTestClient()) {
            final List<Change> commonLog = new ArrayList<>();
            Map<String, List<Change>> changeLogs = new HashMap<>();
            changeLogs.put(null, commonLog);
            
            DatabasesClient client = resource.getClient();
            Query query = client.getAppQuery(AMBIGUOUS_QUERY_ID);
            Rowset rowset = query.execute();
            rowset.setFlowProvider(new DelegatingFlowProvider(rowset.getFlowProvider()){

                @Override
                public List<Change> getChangeLog() {
                    return commonLog;
                }
                
            });
            int oldRowsetSize = rowset.size();
            assertTrue(oldRowsetSize > 1);
            Fields fiedls = rowset.getFields();
            Row row = new Row(fiedls);
            row.setColumnObject(fiedls.find("tid"), NEW_RECORD_ID);
            Field gid = row.getFields().get("gid");
            assertNotNull(gid);
            // original name check
            assertEquals(gid.getName(), "gid");
            assertEquals(gid.getOriginalName(), "ID");
            Field tid = row.getFields().get("tid");
            assertNotNull(tid);
            // original name check
            assertEquals(tid.getName(), "tid");
            assertEquals(tid.getOriginalName(), "ID");
            Field kname = row.getFields().get("kname");
            assertNotNull(kname);
            // original name check
            assertEquals(kname.getName(), "kname");
            assertEquals(kname.getOriginalName(), "NAME");
            // Create operation
            rowset.insertAt(row, true, 1,
                    fiedls.find("gname"), "-g- must be overwritten",
                    fiedls.find("tname"), "-t- must be overwritten",
                    fiedls.find("kname"), "-k- must be overwritten");
            assertNotNull(row.getColumnObject(fiedls.find("gid")));
            assertNotNull(row.getColumnObject(fiedls.find("tid")));
            assertNotNull(row.getColumnObject(fiedls.find("kid")));
            // Update operation
            row.setColumnObject(fiedls.find("gid"), NEW_RECORD_ID);
            // initialization was performed for "tid" field
            row.setColumnObject(fiedls.find("kid"), NEW_RECORD_ID);
            assertEquals(row.getColumnObject(fiedls.find("gid")), NEW_RECORD_ID);
            assertEquals(row.getColumnObject(fiedls.find("tid")), NEW_RECORD_ID);
            assertEquals(row.getColumnObject(fiedls.find("kid")), NEW_RECORD_ID);
            //
            SqlQuery command = client.getAppQuery(COMMAND_QUERY_ID);
            command.putParameter("gid", DataTypeInfo.DECIMAL, NEW_RECORD_ID);
            command.putParameter("gname", DataTypeInfo.VARCHAR, NEW_RECORD_NAME_G);
            SqlCompiledQuery compiled = command.compile();
            compiled.enqueueUpdate();
            assertEquals(1, compiled.getFlow().getChangeLog().size());
            commonLog.addAll(compiled.getFlow().getChangeLog());
            
            //rowset.updateObject(fiedls.find("gname"), NEW_RECORD_NAME_G);
            rowset.updateObject(fiedls.find("tname"), NEW_RECORD_NAME_T);
            rowset.updateObject(fiedls.find("kname"), NEW_RECORD_NAME_K);
            
            client.commit(changeLogs);
            assertTrue(commonLog.isEmpty());
            
            rowset.refresh();
            fiedls = rowset.getFields();
            assertEquals(oldRowsetSize + 1, rowset.size());

            Row newRow = null;
            rowset.beforeFirst();
            while (rowset.next()) {
                if (NEW_RECORD_ID.intValue() == rowset.getInt(fiedls.find("gid"))) {
                    newRow = rowset.getCurrentRow();
                    break;
                }
            }
            assertNotNull(newRow);
            assertEquals(newRow.getColumnObject(fiedls.find("gid")), NEW_RECORD_ID);
            assertEquals(newRow.getColumnObject(fiedls.find("tid")), NEW_RECORD_ID);
            assertEquals(newRow.getColumnObject(fiedls.find("kid")), NEW_RECORD_ID);
            assertEquals(newRow.getColumnObject(fiedls.find("gname")), NEW_RECORD_NAME_G);
            assertEquals(newRow.getColumnObject(fiedls.find("tname")), NEW_RECORD_NAME_T);
            assertEquals(newRow.getColumnObject(fiedls.find("kname")), NEW_RECORD_NAME_K);
            // Delete operation
            rowset.delete();
            client.commit(changeLogs);
            assertTrue(commonLog.isEmpty());
            rowset.refresh();
            fiedls = rowset.getFields();
            assertEquals(oldRowsetSize, rowset.size());

            newRow = null;
            rowset.beforeFirst();
            while (rowset.next()) {
                if (NEW_RECORD_ID.intValue() == rowset.getInt(fiedls.find("gid"))) {
                    newRow = rowset.getCurrentRow();
                    break;
                }
            }
            assertNull(newRow);
        }
    }

    @Test
    public void twoWritableTablesTest() throws Exception {
        try (DatabasesClientWithResource resource = BaseTest.initDevelopTestClient()) {
            final List<Change> commonLog = new ArrayList<>();
            Map<String, List<Change>> changeLogs = new HashMap<>();
            changeLogs.put(null, commonLog);
            
            DatabasesClient client = resource.getClient();
            Query query = client.getAppQuery(AMBIGUOUS_SEMI_WRITABLE_QUERY_ID);
            Rowset rowset = query.execute();
            rowset.setFlowProvider(new DelegatingFlowProvider(rowset.getFlowProvider()){

                @Override
                public List<Change> getChangeLog() {
                    return commonLog;
                }
                
            });
            
            int oldRowsetSize = rowset.size();
            assertTrue(oldRowsetSize > 1);
            Fields fiedls = rowset.getFields();
            Row row = new Row(fiedls);
            row.setColumnObject(fiedls.find("tid"), NEW_RECORD_ID);
            Field gid = row.getFields().get("gid");
            assertNotNull(gid);
            // original name check
            assertEquals(gid.getName(), "gid");
            assertEquals(gid.getOriginalName(), "ID");
            Field tid = row.getFields().get("tid");
            assertNotNull(tid);
            // original name check
            assertEquals(tid.getName(), "tid");
            assertEquals(tid.getOriginalName(), "ID");
            Field kname = row.getFields().get("kname");
            assertNotNull(kname);
            // original name check
            assertEquals(kname.getName(), "kname");
            assertEquals(kname.getOriginalName(), "NAME");
            // Create operation
            rowset.insertAt(row, true, 1,
                    fiedls.find("gname"), "-g- must be overwritten",
                    fiedls.find("tname"), "-t- must be overwritten",
                    fiedls.find("kname"), "-k- must be overwritten");
            assertNotNull(row.getColumnObject(fiedls.find("gid")));
            assertNotNull(row.getColumnObject(fiedls.find("tid")));
            assertNotNull(row.getColumnObject(fiedls.find("kid")));
            // Update operation
            row.setColumnObject(fiedls.find("gid"), NEW_RECORD_ID);
            // initialization was performed for "tid" field
            row.setColumnObject(fiedls.find("kid"), NEW_RECORD_ID);
            assertEquals(row.getColumnObject(fiedls.find("gid")), NEW_RECORD_ID);
            assertEquals(row.getColumnObject(fiedls.find("tid")), NEW_RECORD_ID);
            assertEquals(row.getColumnObject(fiedls.find("kid")), NEW_RECORD_ID);
            //
            rowset.updateObject(fiedls.find("gname"), NEW_RECORD_NAME_G);
            rowset.updateObject(fiedls.find("tname"), NEW_RECORD_NAME_T);
            rowset.updateObject(fiedls.find("kname"), NEW_RECORD_NAME_K);
            client.commit(changeLogs);
            assertTrue(commonLog.isEmpty());
            rowset.refresh();
            fiedls = rowset.getFields();
            assertEquals(oldRowsetSize + 1, rowset.size());

            Row newRow = null;
            rowset.beforeFirst();
            while (rowset.next()) {
                if (NEW_RECORD_ID.intValue() == rowset.getInt(fiedls.find("kid"))) {
                    assertNull(rowset.getInt(fiedls.find("gid")));
                    newRow = rowset.getCurrentRow();
                    break;
                }
            }
            assertNotNull(newRow);
            assertNull(newRow.getColumnObject(fiedls.find("gid")));
            assertEquals(newRow.getColumnObject(fiedls.find("tid")), NEW_RECORD_ID);
            assertEquals(newRow.getColumnObject(fiedls.find("kid")), NEW_RECORD_ID);
            assertNull(newRow.getColumnObject(fiedls.find("gname")));
            assertEquals(newRow.getColumnObject(fiedls.find("tname")), NEW_RECORD_NAME_T);
            assertEquals(newRow.getColumnObject(fiedls.find("kname")), NEW_RECORD_NAME_K);
            // Delete operation
            rowset.delete();
            client.commit(changeLogs);
            assertTrue(commonLog.isEmpty());
            rowset.refresh();
            fiedls = rowset.getFields();
            assertEquals(oldRowsetSize, rowset.size());

            newRow = null;
            rowset.beforeFirst();
            while (rowset.next()) {
                if (NEW_RECORD_ID.intValue() == rowset.getInt(fiedls.find("kid"))) {
                    assertNull(rowset.getInt(fiedls.find("gid")));
                    newRow = rowset.getCurrentRow();
                    break;
                }
            }
            assertNull(newRow);
        }
    }
}
