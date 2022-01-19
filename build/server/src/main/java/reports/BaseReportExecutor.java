package reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import classes.ReportOutAttribute;
import classes.ReportOutCell;
import classes.ReportOutColumn;
import classes.ReportOutOption;
import classes.ReportOutRow;
import classes.ReportOutput;
import d3e.core.ListExt;
import models.ReportConfig;
import models.ReportConfigOption;
import store.EntityHelper;
import store.EntityHelperService;

public abstract class BaseReportExecutor {
  @Autowired
  private EntityHelperService service;
  private EntityHelper helper;

  /**
   * Abstract methods to be implemented.
   */

  protected abstract List<ReportOutRow> getRows(ReportConfig config, String[] cols, List<Object[]> data);

  protected abstract boolean isNumber(String col);

  protected abstract Integer getColumnIndex(String col);

  /*********
   * Methods that need to be selectively overridden. These are methods that should
   * exist only under certain conditions, for example, when datasets are used.
   */

  /**
   * Get the indices of the columns that have SUM aggregate
   * 
   * @return
   */
  protected int[] getSumIndices() {
    return null;
  }

  protected List<String> getUserGivenGroupBys() {
    return ListExt.List();
  }

  /**
   * Check if the current column has an aggregate
   * 
   * @param colName
   * @return
   */
  protected boolean isAggregate(String colName) {
    return false;
  }

  /**
   * This method will keep track of the aggregate values per row.
   * 
   * @param string
   * @param row
   * @param aggregates
   */
  protected void trackAggregate(String string, Object[] row, Map<String, List<Object>> aggregates) {
  }

  protected String getFinalAggregateValue(String key, List<Object> value) {
    return null;
  }

  protected String getAggregateType(String col) {
    return null;
  }

  protected String getDataType(String col) {
    return null;
  }

  /**
   * If a report has includeTotal = true, there will be some extra columns added.
   * This method does that.
   */
  protected void applyIncludeTotals(ReportOutRow row, List<String> datasetColumns, int numCells) {
    if (row == null || numCells == 0) {
      return;
    }

    int numCols = datasetColumns.size();
    int firstCol = numCells;
    int lastCol = row.getCells().size();
    datasetColumns.forEach(one -> {
      ReportOutCell cell = createEmptyCell();
      Double result = 0.0;

      int colIndex = datasetColumns.indexOf(one) + firstCol;
      while (colIndex < lastCol) {
        result += Double.parseDouble(row.getCells().get(colIndex).getValue());
        colIndex += numCols;
      }

      cell.setValue(result.toString());
      row.getCells().add(cell);
    });
  }

  protected List<String> getDatasetColumns(String datasetName) {
    return ListExt.List();
  }

  protected boolean hasIncludeTotals(String datasetName) {
    return false;
  }

  protected String getFilterRef(String filterName) {
    return null;
  }

  protected String getFilterType(String filterName) {
    return null;
  }

  protected boolean hasAllowCustomization() {
    return false;
  }

  protected List<List<Object[]>> getCustomData(ReportConfig config, String datasetName) {
    if (datasetName == null || datasetName.isEmpty()) {
      throw new RuntimeException("Cannot have custom data without a dataset");
    }

    List<List<Object[]>> data = ListExt.List();
    if (haveCompareOptions()) {
      ReportConfigOption opt = find(config, "compareOption_quantity");
      if (opt == null) {
        return data;
      }
      int numCompareOpt = (int) getValueFromString(opt.getValue(), "Integer", null);
      for (int i = 0; i < numCompareOpt; i++) {
        opt = find(config, "compareOption_" + i);
        if (opt == null) {
          throw new RuntimeException("Wrong number of CompareOptions.");
        }
        String optName = (String) getValueFromString(opt.getValue(), "String", null);
        data.add(getCompareOptionData(optName, datasetName));
      }
    }
    return data;
  }

  protected boolean haveCompareOptions() {
    return false;
  }

  protected List<Object[]> getCompareOptionData(String compareOptionName, String datasetName) {
    return ListExt.List();
  }

  /*************
   * Concrete methods to be inherited.
   */

  /*********** Data conversion methods ***********/
  protected Object getValueFromString(String value, String type, String refName) {
    switch (type) {
    case "Integer":
      return Long.parseLong(value);
    case "Boolean":
      return Boolean.parseBoolean(value);
    case "Double":
      return Double.parseDouble(value);
    case "String":
      return value;
    case "Ref":
      this.helper = service.get(refName);
      return helper.getById(Long.parseLong(value));
    default:
      return null;
    }
  }

  protected String convertToString(Object value) {
    // TODO
    return value.toString();
  }

  /*********** Aggregate methods ***********/
  protected void updateSumAggregate(List<Object> data, String colName, String valueType,
      Map<String, List<Object>> aggregates) {
    Object trackedValue = getTrackedValue(colName, valueType, 0, false, aggregates);

    // Only one element in the list
    Object untrackedValue = getUntrackedValue(data, valueType, 0, false);

    aggregates.put(colName, ListExt.asList(add(trackedValue, untrackedValue)));
  }

  protected void updateCountAggregate(List<Object> data, String colName, String valueType,
      Map<String, List<Object>> aggregates) {
    Object trackedValue = getTrackedValue(colName, valueType, 0, true, aggregates);

    // Only one element in the list
    Object untrackedValue = getUntrackedValue(data, valueType, 0, true);

    aggregates.put(colName, ListExt.asList(add(trackedValue, untrackedValue)));
  }

  protected void updateAverageAggregate(List<Object> data, String colName, String valueType,
      Map<String, List<Object>> aggregates) {
    // Two elements in the list
    Object trackedSum = getTrackedValue(colName, valueType, 0, false, aggregates);
    Object untrackedSum = getUntrackedValue(data, valueType, 0, false);

    Object trackedCount = getTrackedValue(colName, valueType, 1, true, aggregates);
    Object untrackedCount = getUntrackedValue(data, valueType, 1, true);
    aggregates.put(colName, ListExt.asList(add(trackedSum, untrackedSum), add(trackedCount, untrackedCount)));
  }

  protected <T> void updateMinAggregate(List<Object> data, String colName, String valueType,
      Map<String, List<Object>> aggregates, Comparator<T> compare) {
    Object trackedValue = getTrackedValue(colName, valueType, 0, false, aggregates);

    // Only one element in the list
    Object untrackedValue = getUntrackedValue(data, valueType, 0, false);

    aggregates.put(colName,
        ListExt.asList(compare.compare((T) trackedValue, (T) untrackedValue) < 0 ? trackedValue : untrackedValue));
  }

  protected <T> void updateMaxAggregate(List<Object> data, String colName, String valueType,
      Map<String, List<Object>> aggregates, Comparator<T> compare) {
    Object trackedValue = getTrackedValue(colName, valueType, 0, false, aggregates);

    // Only one element in the list
    Object untrackedValue = getUntrackedValue(data, valueType, 0, false);

    aggregates.put(colName,
        ListExt.asList(compare.compare((T) trackedValue, (T) untrackedValue) > 0 ? trackedValue : untrackedValue));
  }

  protected void updateArrayAggregate(List<Object> data, String colName, String valueType,
      Map<String, List<Object>> aggregates) {
    // TODO
  }

  protected void updateStringAggregate(List<Object> data, String colName, String valueType,
      Map<String, List<Object>> aggregates) {
    // TODO
  }

  protected void updatePercentAggregate(List<Object> data, String colName, String valueType,
      Map<String, List<Object>> aggregates) {
    // TODO
  }

  /*******************************/

  protected ReportConfigOption find(ReportConfig config, String key) {
    if (config == null) {
      return null;
    }
    Optional<ReportConfigOption> option = config.getValues().stream().filter((v) -> v.getIdentity().equals(key))
        .findFirst();
    return option.orElse(null);
  }

  protected ReportOutput format(ReportConfig config, List<Object[]> data, String[] cols) {
    ReportOutput result = new ReportOutput();
    if (cols == null || cols.length == 0) {
      return result;
    }
    ReportConfigOption dataset = find(config, "dataset");
    if (dataset != null) {
      int datasetIndex = data.get(0).length - 1;
      List<String> datasetFilterValues = getDatasetValues(data, datasetIndex);
      result.setColumns(getDatasetFilterColumns(dataset.getValue(), cols.length, datasetFilterValues));
    }
    result.setSubColumns(getColumns(cols, dataset));
    result.setRows(getRows(config, cols, data));
    result.setOptions(config.getValues().stream().map((opt) -> {
      return new ReportOutOption(opt.getIdentity(), opt.getValue());
    }).collect(Collectors.toList()));
    return result;
  }

  protected List<ReportOutColumn> getColumns(String[] cols, ReportConfigOption dataset) {
    List<ReportOutColumn> result = new ArrayList<ReportOutColumn>();
    for (String col : cols) {
      result.add(createColumn(col, 2, 1));
    }
    if (dataset != null) {
      List<String> datasetColumns = getDatasetColumns(dataset.getValue());
      boolean includeTotals = hasIncludeTotals(dataset.getValue());
      for (String col : datasetColumns) {
        result.add(createColumn(col, 1, 1));
      }
      if (includeTotals) {
        for (String col : datasetColumns) {
          result.add(createColumn(col, 2, 1));
        }
      }
    }
    return result;
  }

  protected List<ReportOutCell> getCells(ReportConfig config, String[] cols, Object[] row) {
    List<ReportOutCell> result = new ArrayList<ReportOutCell>();
    if (row.length == 0) {
      return result;
    }
    for (String col : cols) {
      ReportOutCell one = new ReportOutCell();
      one.setValue(convertToString(getValueFromRow(row, col)));
      one.setKey(null);
      one.setAttributes(ListExt.asList(new ReportOutAttribute("alignment", isNumber(col) ? "right" : "left")));
      one.setType(null);
      result.add(one);
    }
    return result;
  }

  protected List<ReportOutRow> applySelectedColumns(ReportConfig config, String[] cols, List<Object[]> data) {
    Integer rowKey = 1;
    List<ReportOutRow> result = new ArrayList<ReportOutRow>();
    if (data.isEmpty()) {
      return result;
    }
    for (Object[] row : data) {
      ReportOutRow one = new ReportOutRow();
      one.setKey(rowKey.toString());
      rowKey++;
      one.setParentKey(null);
      one.setCells(getCells(config, cols, row));
      result.add(one);
    }
    return result;
  }

  protected List<ReportOutRow> applyDataset(ReportConfig config, List<ReportOutRow> rows, List<Object[]> data,
      List<List<Object[]>> customData, List<String> datasetColumns, String[] cols, boolean includeTotals) {
    // This happens after applySelectedColumns
    if (datasetColumns.isEmpty()) {
      return rows;
    }

    List<ReportOutRow> result = new ArrayList<ReportOutRow>();

    // The extra column will always be at the last
    int datasetIndex = data.get(0).length - 1;

    // Get the values currently in the DB for the dataset filter column
    List<String> datasetFilterValues = getDatasetValues(data, datasetIndex);

    List<String> userGB = getUserGivenGroupBys();
    List<Integer> userGBIndices = userGB.stream().map(one -> getColumnIndex(one)).collect(Collectors.toList());
    Collections.reverse(userGBIndices);

    List<Integer> datasetColIndices = datasetColumns.stream().map(one -> getColumnIndex(one))
        .collect(Collectors.toList());

    int rowIndex = 0;
    int dataRowIndex = 0;
    int compareDataRowIndex = 0;
    ReportOutRow row = null;
    ReportOutRow resultRow = null;
    Map<String, List<Object>> aggregates = new HashMap<>();

    while (rowIndex < rows.size()) {
      if (hasRowChanged(data.get(dataRowIndex), resultRow, userGBIndices)) {
        // TODO: Need logic for adjusting keys and parent keys here
        // Some extra work is happening here. Maybe this method should not follow
        // applySelectedColumns

        // Add the calculated aggregate into the previous row here.
        for (Map.Entry<String, List<Object>> entry : aggregates.entrySet()) {
          insertCellIntoRow(resultRow, getColumnIndex(entry.getKey()),
              getFinalAggregateValue(entry.getKey(), entry.getValue()));
        }
        aggregates.clear();

        if (resultRow != null) {
          result.add(resultRow);
          rowIndex++;
        }
        row = rows.get(rowIndex);

        // Row has changed. So create a new one.
        resultRow = new ReportOutRow(ListExt.List(), row.getGroupingKey(), row.getKey(), row.getParentKey());

        // Step 1 - Add the non-dataset columns to the row
        for (ReportOutCell cell : row.getCells()) {
          int cellIndex = row.getCells().indexOf(cell);
          if (isAggregate(cols[cellIndex])) {
            // Handle aggregate calculation here
            trackAggregate(cols[cellIndex], data.get(dataRowIndex), aggregates);
          } else {
            resultRow.getCells().add(cell);
          }
        }
      }

      // Step 2 - Add the dataset columns to the row
      Object[] currentData = data.get(dataRowIndex);
      boolean allowCust = hasAllowCustomization();
      List<Object[]> compareDataList = ListExt.List();
      if (allowCust) {
        for (List<Object[]> oneCustomData : customData) {
          int rowComp = 0;
          Object[] currentCompareData;
          do {
            currentCompareData = oneCustomData.get(compareDataRowIndex);
            rowComp = compare(currentData, currentCompareData, datasetIndex, userGBIndices);
          } while (rowComp < 0);
          compareDataList.add(currentCompareData);
        }
      } else {
        compareDataList.add(null);
      }
      for (Object[] currentCompareData : compareDataList) {
        readDatasetValues(config, resultRow, currentData, currentCompareData, datasetColumns, datasetColIndices,
            datasetFilterValues, datasetIndex, dataRowIndex, compareDataRowIndex, cols.length, allowCust);
      }

      if (includeTotals) {
        applyIncludeTotals(resultRow, datasetColumns, row.getCells().size());
      }

      dataRowIndex++;
    }

    return result;
  }

  private int compare(Object[] currentData, Object[] currentCompareData, int datasetIndex,
      List<Integer> userGBIndices) {
    // TODO Auto-generated method stub
    return 0;
  }

  protected void readDatasetValues(ReportConfig config, ReportOutRow row, Object[] currentData,
      Object[] currentCompareData, List<String> datasetCols, List<Integer> datasetColIndices,
      List<String> datasetFilterValues, int datasetIndex, int dataRowIndex, int compareDataRowIndex, int startIndex,
      boolean allowCust) {
    String datasetValue = convertToString(currentData[datasetIndex]); // The value of the manual groupBy (think
                                                                      // PaymentMode)
    int numCellsInserted = 0;
    for (String col : datasetCols) {
      int topIndex = datasetFilterValues.indexOf(datasetValue), // The index of the filter (Card/Cash/UPI etc.)
          bottomIndex = datasetCols.indexOf(col); // The index of the column under one filter.
      int cellIndex = startIndex + topIndex * datasetCols.size() + bottomIndex + numCellsInserted;

      String dataToAdd = convertToString(currentData[datasetColIndices.get(bottomIndex)]);
      insertCellIntoRow(row, cellIndex, dataToAdd);

      if (allowCust) {
        numCellsInserted = handleDatasetCustomization(config, row, currentCompareData, datasetColIndices,
            numCellsInserted, cellIndex, bottomIndex);
      }
    }
  }

  private int handleDatasetCustomization(ReportConfig config, ReportOutRow row, Object[] currentCompareData,
      List<Integer> datasetColIndices, int numCellsInserted, int cellIndex, int bottomIndex) {
    // If the allowCustomization boolean was not checked, we don't do this at all.
    int compareIndex = cellIndex + 1;
    String dataToAdd = currentCompareData == null ? "0"
        : convertToString(currentCompareData[datasetColIndices.get(bottomIndex)]);
    insertCellIntoRow(row, compareIndex, dataToAdd);
    numCellsInserted++;

    // CompareOptions columns here
    ReportConfigOption opt = find(config, "compareColumns_<ReportCompareOption_ID>_number");
    int numCompareCols = 0;
    if (opt != null) {
      numCompareCols = (int) getValueFromString(opt.getValue(), "Integer", null);
    }

    int compareResIndex = compareIndex + 1;
    for (int i = 0; i < numCompareCols; i++) {
      ReportConfigOption colOpt = find(config, "compareColumns_<ReportCompareOption_ID>_" + i);
      if (colOpt == null) {
        throw new RuntimeException("Wrong number of compare columns provided.");
      }
      String colType = (String) getValueFromString(colOpt.getValue(), "String", null);
      switch (colType) {
      case "INTEGER": {
        int lval = (int) getValueFromString(row.getCells().get(cellIndex).getValue(), "Integer", null),
            rval = (int) getValueFromString(row.getCells().get(compareIndex).getValue(), "Integer", null);
        dataToAdd = convertToString(lval - rval);
        break;
      }
      case "DOUBLE": {
        double lval = (double) getValueFromString(row.getCells().get(cellIndex).getValue(), "Integer", null),
            rval = (double) getValueFromString(row.getCells().get(compareIndex).getValue(), "Integer", null);
        dataToAdd = convertToString(lval - rval);
        break;
      }
      case "PERCENT": {
        double lval = (double) getValueFromString(row.getCells().get(cellIndex).getValue(), "Integer", null),
            rval = (double) getValueFromString(row.getCells().get(compareIndex).getValue(), "Integer", null);
        dataToAdd = convertToString((lval - rval) / lval);
        break;
      }
      default:
        throw new RuntimeException("Invalid compare column type.");
      }

      insertCellIntoRow(row, compareResIndex++, dataToAdd);
    }

    return numCellsInserted;
  }

  protected String[] getSelectedColumns(ReportConfig config) {
    ReportConfigOption colsOpt = find(config, "selected_columns");
    if (colsOpt == null) {
      return null;
    }
    String colsStr = colsOpt.getValue();
    if (colsStr.isEmpty()) {
      return new String[] {};
    }
    return colsStr.split(",\\s?");
  }

  protected List<ReportOutRow> applyGrouping(ReportConfig config, List<ReportOutRow> rows, List<Object[]> data) {
    ReportConfigOption grp = find(config, "allowedGroup_0");
    if (grp == null) {
      return rows;
    }
    int columnIndex = getColumnIndex(grp.getValue());

    for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
      ReportOutRow one = rows.get(rowIndex);
      one.setGroupingKey((String) data.get(rowIndex)[columnIndex]);
    }

    rows.sort(new Comparator<ReportOutRow>() {
      public int compare(ReportOutRow one, ReportOutRow two) {
        return one.getGroupingKey().compareTo(two.getGroupingKey());
      }
    });

    return prepareGroupingRows(rows);
  }

  protected <T> T getFilterValue(ReportConfig config, String filterString, String filterName) {
    ReportConfigOption opt = find(config, filterString);
    if (opt == null) {
      throw new RuntimeException("Filter not provided");
    }
    return (T) getValueFromString(opt.getValue(), getFilterType(filterName), getFilterRef(filterName));
  }

  protected boolean getFilterSupportAll(ReportConfig config, String filterAllString) {
    ReportConfigOption opt = find(config, filterAllString);
    if (opt == null) {
      return false;
    }
    return Boolean.valueOf(opt.getValue());
  }

  /*********** Helper methods ***********/

  private Object getValueFromRow(Object[] row, String col) {
    return row[getColumnIndex(col)];
  }

  private Object add(Object one, Object two) {
    if (one == null || two == null) {
      throw new RuntimeException("Nulls cannot be added.");
    }

    if (one instanceof Integer) {
      return (int) one + (int) two;
    }

    if (one instanceof Double) {
      return (double) one + (double) two;
    }

    if (one instanceof String && two instanceof String) {
      return (String) one + (String) two;
    }

    throw new RuntimeException("Cannot add given objects: " + one + ", " + two);
  }

  private Object getDefaultValue(String valueType) {
    switch (valueType) {
    case "Integer":
      return 0;
    case "Double":
      return 0.0;
    case "String":
      return "";
    default:
      return null;
    }
  }

  private Object getTrackedValue(String colName, String valueType, int index, boolean forCount,
      Map<String, List<Object>> aggregates) {
    List<Object> existing = aggregates.getOrDefault(colName, null);
    if (existing != null && !existing.isEmpty()) {
      // If there is an existing value, return it.
      return existing.get(index);
    }

    // The default value returned will differ for count
    return forCount ? 0 : getDefaultValue(valueType);
  }

  private Object getUntrackedValue(List<Object> data, String valueType, int index, boolean forCount) {
    if (forCount) {
      return data.isEmpty() ? 0 : 1;
    }
    return data.isEmpty() ? getDefaultValue(valueType) : data.get(index);
  }

  private List<ReportOutColumn> getDatasetFilterColumns(String dataset, int numCols, List<String> datasetFilterValues) {
    // TODO: Confirm the spans and column structure
    List<ReportOutColumn> result = new ArrayList<ReportOutColumn>(
        Collections.nCopies(numCols, createColumn(null, 0, 1)));
    result.addAll(datasetFilterValues.stream().map(one -> createColumn(one, 1, numCols)).collect(Collectors.toList()));
    boolean includeTotals = hasIncludeTotals(dataset);
    if (includeTotals) {
      result.addAll(
          datasetFilterValues.stream().map(one -> createColumn("total", 1, numCols)).collect(Collectors.toList()));
    }
    return result;
  }

  private boolean hasRowChanged(Object[] dataRow, ReportOutRow outRow, List<Integer> userGBIndices) {
    if (dataRow == null || outRow == null) {
      return true;
    }
    for (Integer i : userGBIndices) {
      if (i == -1) {
        // If the group by is not part of the selected columns, ignore it.
        continue;
      }
      if (!convertToString(dataRow[i]).equals(outRow.getCells().get(i).getValue())) {
        // If any groupBy value has changed, we are in a different row.
        return true;
      }
    }
    return false;
  }

  private void insertCellIntoRow(ReportOutRow resultRow, int cellIndex, String dataToAdd) {
    int size = resultRow.getCells().size();
    if (size <= cellIndex) {
      addNEmptyCells(cellIndex - size + 1, resultRow.getCells());
    }

    resultRow.getCells().get(cellIndex).setValue(dataToAdd);
  }

  private List<String> getDatasetValues(List<Object[]> data, int columnIndex) {
    // TODO: Is there a better algorithm to do this?
    Set<String> values = data.stream().map(one -> convertToString(one[columnIndex])).collect(Collectors.toSet());
    List<String> result = new ArrayList<String>();
    result.addAll(values);
    return result;
  }

  private List<ReportOutRow> prepareGroupingRows(List<ReportOutRow> rows) {
    if (rows.isEmpty()) {
      return rows;
    }
    Integer parent = 0;
    int index = 0;
    Integer rowKey = 1;
    String groupKey = null;
    List<ReportOutRow> result = new ArrayList<>();
    for (ReportOutRow row : rows) {
      if (!row.getGroupingKey().equals(groupKey)) {
        if (groupKey != null) {
          result.add(createFooterRow(rows, rowKey++, parent, rows.get(index).getCells().size()));
        }
        groupKey = row.getGroupingKey();
        result.add(createGroupRow(rowKey, row.getGroupingKey(), row.getCells().size()));
        parent = rowKey;
        rowKey++;
        result.add(createEmptyRow(rowKey++, parent, row.getCells().size()));
      }
      result.add(row);
      row.setKey(rowKey.toString());
      row.setParentKey(parent.toString());
      rowKey++;
    }
    result.add(createFooterRow(rows, rowKey, parent, rows.get(index).getCells().size()));
    return result;
  }

  private ReportOutRow createGroupRow(Integer currentRowKey, String value, int numCols) {
    ReportOutRow first = new ReportOutRow();
    first.setKey(currentRowKey.toString());
    first.setParentKey(null);
    currentRowKey++;
    List<ReportOutCell> cells = ListExt.asList(new ReportOutCell(new ArrayList<>(), "", null, value));
    addNEmptyCells(numCols - 1, cells);
    first.setCells(cells);
    return first;
  }

  private ReportOutRow createFooterRow(List<ReportOutRow> rows, Integer rowKey, Integer parentKey, int numCols) {
    ReportOutRow footer = new ReportOutRow();
    footer.setKey(rowKey.toString());
    footer.setParentKey(parentKey.toString());
    footer.setCells(ListExt.List());
    int[] sumIndices = getSumIndices();
    if (sumIndices.length > 0) {
      int index = 0;
      for (int i = 0; i < numCols; i++) {
        if (i == sumIndices[index]) {
          Double total = calculateSum(rows, i);
          ReportOutCell cell = createEmptyCell();
          cell.setValue(convertToString(total));
          index++;
        } else {
          footer.getCells().add(createEmptyCell());
        }
      }
    } else {
      addNEmptyCells(numCols, footer.getCells());
    }
    return footer;
  }

  private ReportOutColumn createColumn(String value, Integer rowSpan, Integer colSpan) {
    ReportOutColumn one = new ReportOutColumn();
    one.setAttributes(ListExt.asList(new ReportOutAttribute("isBold", "true"),
        new ReportOutAttribute("alignment", isNumber(value) ? "right" : "left"),
        new ReportOutAttribute("rowSpan", rowSpan.toString()), new ReportOutAttribute("colSpan", colSpan.toString())));
    one.setType(null);
    one.setValue(value);
    return one;
  }

  private ReportOutRow createEmptyRow(Integer currentRowKey, Integer currentParentKey, int numCols) {
    ReportOutRow empty = new ReportOutRow();
    empty.setKey(currentRowKey.toString());
    empty.setParentKey(currentParentKey.toString());
    currentRowKey++;
    empty.setCells(ListExt.List());
    addNEmptyCells(numCols, empty.getCells());
    return empty;
  }

  private ReportOutCell createEmptyCell() {
    ReportOutCell emptyCell = new ReportOutCell();
    emptyCell.setKey("");
    emptyCell.setType(null);
    emptyCell.setValue("");
    emptyCell.setAttributes(new ArrayList<ReportOutAttribute>());
    return emptyCell;
  }

  private Double calculateSum(List<ReportOutRow> rows, int i) {
    Double result = 0.0;
    for (ReportOutRow row : rows) {
      result += Double.parseDouble(row.getCells().get(i).getValue());
    }
    return result;
  }

  private void addNEmptyCells(int n, List<ReportOutCell> result) {
    for (int i = 0; i < n; i++) {
      result.add(createEmptyCell());
    }
  }
}
