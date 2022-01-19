package rest;

import classes.DBResult;
import classes.LoginResult;
import classes.ReportOutAttribute;
import classes.ReportOutCell;
import classes.ReportOutColumn;
import classes.ReportOutOption;
import classes.ReportOutRow;
import classes.ReportOutput;
import d3e.core.SchemaConstants;
import gqltosql.schema.DModel;
import gqltosql.schema.FieldPrimitiveType;
import java.util.HashMap;
import java.util.Map;

public class StructSchema1 {
  private Map<String, DModel<?>> allTypes = new HashMap<>();

  public StructSchema1(Map<String, DModel<?>> allTypes) {
    this.allTypes = allTypes;
  }

  public void createAllTables() {
    addReportOutputFields();
    addReportOutOptionFields();
    addReportOutColumnFields();
    addReportOutAttributeFields();
    addReportOutRowFields();
    addReportOutCellFields();
    addDBResultFields();
    addLoginResultFields();
  }

  public DModel<?> getType(String type) {
    return allTypes.get(type);
  }

  public <T> DModel<T> getType2(String type) {
    return ((DModel<T>) allTypes.get(type));
  }

  private void addReportOutputFields() {
    DModel<ReportOutput> m = getType2("ReportOutput");
    m.addReferenceCollection(
        "options",
        ReportOutput._OPTIONS,
        null,
        null,
        true,
        getType("ReportOutOption"),
        (s) -> s.getOptions(),
        (s, v) -> s.setOptions(v));
    m.addReferenceCollection(
        "columns",
        ReportOutput._COLUMNS,
        null,
        null,
        true,
        getType("ReportOutColumn"),
        (s) -> s.getColumns(),
        (s, v) -> s.setColumns(v));
    m.addReferenceCollection(
        "subColumns",
        ReportOutput._SUBCOLUMNS,
        null,
        null,
        true,
        getType("ReportOutColumn"),
        (s) -> s.getSubColumns(),
        (s, v) -> s.setSubColumns(v));
    m.addReferenceCollection(
        "attributes",
        ReportOutput._ATTRIBUTES,
        null,
        null,
        true,
        getType("ReportOutAttribute"),
        (s) -> s.getAttributes(),
        (s, v) -> s.setAttributes(v));
    m.addReferenceCollection(
        "rows",
        ReportOutput._ROWS,
        null,
        null,
        true,
        getType("ReportOutRow"),
        (s) -> s.getRows(),
        (s, v) -> s.setRows(v));
  }

  private void addReportOutOptionFields() {
    DModel<ReportOutOption> m = getType2("ReportOutOption");
    m.addPrimitive(
        "key",
        ReportOutOption._KEY,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getKey(),
        (s, v) -> s.setKey(v));
    m.addPrimitive(
        "value",
        ReportOutOption._VALUE,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getValue(),
        (s, v) -> s.setValue(v));
  }

  private void addReportOutColumnFields() {
    DModel<ReportOutColumn> m = getType2("ReportOutColumn");
    m.addPrimitive(
        "type",
        ReportOutColumn._TYPE,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getType(),
        (s, v) -> s.setType(v));
    m.addPrimitive(
        "value",
        ReportOutColumn._VALUE,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getValue(),
        (s, v) -> s.setValue(v));
    m.addReferenceCollection(
        "attributes",
        ReportOutColumn._ATTRIBUTES,
        null,
        null,
        true,
        getType("ReportOutAttribute"),
        (s) -> s.getAttributes(),
        (s, v) -> s.setAttributes(v));
  }

  private void addReportOutAttributeFields() {
    DModel<ReportOutAttribute> m = getType2("ReportOutAttribute");
    m.addPrimitive(
        "key",
        ReportOutAttribute._KEY,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getKey(),
        (s, v) -> s.setKey(v));
    m.addPrimitive(
        "value",
        ReportOutAttribute._VALUE,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getValue(),
        (s, v) -> s.setValue(v));
  }

  private void addReportOutRowFields() {
    DModel<ReportOutRow> m = getType2("ReportOutRow");
    m.addPrimitive(
        "key",
        ReportOutRow._KEY,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getKey(),
        (s, v) -> s.setKey(v));
    m.addPrimitive(
        "parentKey",
        ReportOutRow._PARENTKEY,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getParentKey(),
        (s, v) -> s.setParentKey(v));
    m.addReferenceCollection(
        "cells",
        ReportOutRow._CELLS,
        null,
        null,
        true,
        getType("ReportOutCell"),
        (s) -> s.getCells(),
        (s, v) -> s.setCells(v));
    m.addPrimitive(
        "groupingKey",
        ReportOutRow._GROUPINGKEY,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getGroupingKey(),
        (s, v) -> s.setGroupingKey(v));
  }

  private void addReportOutCellFields() {
    DModel<ReportOutCell> m = getType2("ReportOutCell");
    m.addPrimitive(
        "key",
        ReportOutCell._KEY,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getKey(),
        (s, v) -> s.setKey(v));
    m.addPrimitive(
        "type",
        ReportOutCell._TYPE,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getType(),
        (s, v) -> s.setType(v));
    m.addPrimitive(
        "value",
        ReportOutCell._VALUE,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getValue(),
        (s, v) -> s.setValue(v));
    m.addReferenceCollection(
        "attributes",
        ReportOutCell._ATTRIBUTES,
        null,
        null,
        true,
        getType("ReportOutAttribute"),
        (s) -> s.getAttributes(),
        (s, v) -> s.setAttributes(v));
  }

  private void addDBResultFields() {
    DModel<DBResult> m = getType2("DBResult");
    m.addEnum(
        "status",
        DBResult._STATUS,
        null,
        SchemaConstants.DBResultStatus,
        (s) -> s.getStatus(),
        (s, v) -> s.setStatus(v));
    m.addPrimitiveCollection(
        "errors",
        DBResult._ERRORS,
        null,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getErrors(),
        (s, v) -> s.setErrors(v));
  }

  private void addLoginResultFields() {
    DModel<LoginResult> m = getType2("LoginResult");
    m.addPrimitive(
        "success",
        LoginResult._SUCCESS,
        null,
        FieldPrimitiveType.Boolean,
        (s) -> s.isSuccess(),
        (s, v) -> s.setSuccess(v));
    m.addReference(
        "userObject",
        LoginResult._USEROBJECT,
        null,
        false,
        getType("User"),
        (s) -> s.getUserObject(),
        (s, v) -> s.setUserObject(v),
        (s) -> s.getUserObjectRef());
    m.addPrimitive(
        "token",
        LoginResult._TOKEN,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getToken(),
        (s, v) -> s.setToken(v));
    m.addPrimitive(
        "failureMessage",
        LoginResult._FAILUREMESSAGE,
        null,
        FieldPrimitiveType.String,
        (s) -> s.getFailureMessage(),
        (s, v) -> s.setFailureMessage(v));
  }
}
