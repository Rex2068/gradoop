package org.gradoop.flink.io.impl.graph;

import com.google.common.collect.Maps;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.gradoop.common.model.impl.properties.Properties;
import org.gradoop.flink.io.impl.graph.tuples.ImportEdge;
import org.gradoop.flink.io.impl.graph.tuples.ImportVertex;
import org.gradoop.flink.model.GradoopFlinkTestBase;
import org.gradoop.flink.model.impl.LogicalGraph;
import org.junit.Test;

import java.util.Map;

public class GraphDataSourceTest extends GradoopFlinkTestBase {

  @Test
  public void testReadStructureOnly() throws Exception {
    ExecutionEnvironment env = getExecutionEnvironment();

    DataSet<ImportVertex<Long>> importVertices = env.fromElements(
      new ImportVertex<>(0L),
      new ImportVertex<>(1L));

    DataSet<ImportEdge<Long>> importEdges = env.fromElements(
      new ImportEdge<>(0L, 0L, 1L));

    LogicalGraph expected = getLoaderFromString("expected[()-->()]")
      .getLogicalGraphByVariable("expected");

    GraphDataSource<Long> dataSource = new GraphDataSource<>(
      importVertices, importEdges, getConfig());

    LogicalGraph output = dataSource.getLogicalGraph();

    collectAndAssertTrue(output.equalsByElementData(expected));
  }

  @Test
  public void testReadWithLabel() throws Exception {
    ExecutionEnvironment env = getExecutionEnvironment();

    DataSet<ImportVertex<Long>> importVertices = env.fromElements(
      new ImportVertex<>(0L, "A"),
      new ImportVertex<>(1L, "B"));

    DataSet<ImportEdge<Long>> importEdges = env.fromElements(
      new ImportEdge<>(0L, 0L, 1L, "a"));

    LogicalGraph expected = getLoaderFromString("expected[(:A)-[:a]->(:B)]")
      .getLogicalGraphByVariable("expected");

    GraphDataSource<Long> dataSource = new GraphDataSource<>(
      importVertices, importEdges, getConfig());

    LogicalGraph output = dataSource.getLogicalGraph();

    collectAndAssertTrue(output.equalsByElementData(expected));
  }

  @Test
  public void testReadWithLabelAndProperties() throws Exception {
    ExecutionEnvironment env = getExecutionEnvironment();

    Map<String, Object> properties = Maps.newHashMap();
    properties.put("foo", 42);

    DataSet<ImportVertex<Long>> importVertices = env.fromElements(
      new ImportVertex<>(0L, "A", Properties.createFromMap(properties)),
      new ImportVertex<>(1L, "B", Properties.createFromMap(properties)));

    DataSet<ImportEdge<Long>> importEdges = env.fromElements(
      new ImportEdge<>(0L, 0L, 1L, "a", Properties.createFromMap(properties)),
      new ImportEdge<>(1L, 1L, 0L, "b", Properties.createFromMap(properties)));

    LogicalGraph expected =
      getLoaderFromString("expected[" +
        "(a:A {foo : 42})" +
        "(b:B {foo : 42})" +
        "(a)-[:a {foo : 42}]->(b)-[:b {foo : 42}]->(a)" +
        "]").getLogicalGraphByVariable("expected");

    GraphDataSource<Long> dataSource = new GraphDataSource<>(
      importVertices, importEdges, getConfig());

    LogicalGraph output = dataSource.getLogicalGraph();

    collectAndAssertTrue(output.equalsByElementData(expected));
  }

  @Test
  public void testReadWithLineage() throws Exception {
    ExecutionEnvironment env = getExecutionEnvironment();

    Map<String, Object> properties = Maps.newHashMap();
    properties.put("foo", 42);

    DataSet<ImportVertex<Long>> importVertices = env.fromElements(
      new ImportVertex<>(0L, "A", Properties.createFromMap(properties)),
      new ImportVertex<>(1L, "B", Properties.createFromMap(properties)));

    DataSet<ImportEdge<Long>> importEdges = env.fromElements(
      new ImportEdge<>(0L, 0L, 1L, "a", Properties.createFromMap(properties)),
      new ImportEdge<>(1L, 1L, 0L, "b", Properties.createFromMap(properties)));

    LogicalGraph expected = getLoaderFromString("expected[" +
        "(a:A {foo : 42, __L : 0L})" +
        "(b:B {foo : 42, __L : 1L})" +
        "(a)-[:a {foo : 42, __L : 0L}]->(b)-[:b {foo : 42, __L : 1L}]->(a)" +
        "]").getLogicalGraphByVariable("expected");

    GraphDataSource<Long> dataSource = new GraphDataSource<>(
      importVertices, importEdges, "__L", getConfig());

    LogicalGraph output = dataSource.getLogicalGraph();

    collectAndAssertTrue(output.equalsByElementData(expected));
  }
}
