/*
 * This file is part of Gradoop.
 *
 * Gradoop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gradoop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gradoop. If not, see <http://www.gnu.org/licenses/>.
 */

package org.gradoop.utils.statistics;

import org.apache.flink.api.common.ProgramDescription;
import org.gradoop.examples.AbstractRunner;
import org.gradoop.flink.model.impl.operators.matching.common.statistics.GraphStatisticsReader;
import org.gradoop.flink.model.impl.operators.statistics.VertexLabelDistribution;

/**
 * Computes {@link VertexLabelDistribution} for a given logical graph.
 */
public class VertexLabelDistributionRunner extends AbstractRunner implements ProgramDescription {

  /**
   * args[0] - path to input directory
   * args[1] - input format (json, csv)
   * args[2] - path to output directory
   *
   * @param args arguments
   * @throws Exception if something goes wrong
   */
  public static void main(String[] args) throws Exception {
    new VertexLabelDistribution()
      .execute(readLogicalGraph(args[0], args[1]))
      .writeAsCsv(appendSeparator(args[2]) + GraphStatisticsReader.FILE_VERTEX_COUNT_BY_LABEL,
        System.lineSeparator(), GraphStatisticsReader.TOKEN_SEPARATOR)
      .setParallelism(1);

    getExecutionEnvironment().execute("Statistics: Vertex label distribution");
  }

  @Override
  public String getDescription() {
    return VertexLabelDistributionRunner.class.getName();
  }
}
