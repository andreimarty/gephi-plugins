/*
Copyright 2008-2011 Gephi
Authors : Luiz Ribeiro <luizribeiro@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.scripting.wrappers;

import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.api.Range;
import org.gephi.filters.plugin.graph.DegreeRangeBuilder;
import org.gephi.filters.plugin.graph.DegreeRangeBuilder.DegreeRangeFilter;
import org.gephi.filters.plugin.graph.InDegreeRangeBuilder;
import org.gephi.filters.plugin.graph.InDegreeRangeBuilder.InDegreeRangeFilter;
import org.gephi.filters.plugin.graph.OutDegreeRangeBuilder;
import org.gephi.filters.plugin.graph.OutDegreeRangeBuilder.OutDegreeRangeFilter;
import org.gephi.scripting.util.GyNamespace;
import org.openide.util.Lookup;
import org.python.core.Py;
import org.python.core.PyObject;

/**
 *
 * @author Luiz Ribeiro
 */
public class GyAttributeTopology extends GyAttribute {

    protected Type topologyType;

    public static enum Type {

        DEGREE,
        IN_DEGREE,
        OUT_DEGREE
    }

    public GyAttributeTopology(GyNamespace namespace, Type topologyType) {
        super(namespace);
        this.topologyType = topologyType;
    }

    @Override
    public String toString() {
        String typeName;

        if (topologyType == Type.DEGREE) {
            typeName = "degree";
        } else if (topologyType == Type.IN_DEGREE) {
            typeName = "indegree";
        } else if (topologyType == Type.OUT_DEGREE) {
            typeName = "outdegree";
        } else {
            typeName = "UNKNOWN";
        }

        return "Node Attribute '" + typeName + "' (" + getAttributeType().getSimpleName() + ")";
    }

    @Override
    public Class getAttributeType() {
        return Integer.class;
    }

    @Override
    public boolean isNodeAttribute() {
        return true;
    }

    @Override
    protected Query buildRangeQuery(Range range) {
        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
        Query query;

        if (topologyType == Type.DEGREE) {
            DegreeRangeFilter degreeRangeFilter = new DegreeRangeBuilder.DegreeRangeFilter();
            query = filterController.createQuery(degreeRangeFilter);
            degreeRangeFilter.setRange(range);
        } else if (topologyType == Type.IN_DEGREE) {
            InDegreeRangeFilter inDegreeRangeFilter = new InDegreeRangeBuilder.InDegreeRangeFilter();
            query = filterController.createQuery(inDegreeRangeFilter);
            inDegreeRangeFilter.setRange(range);
        } else if (topologyType == Type.OUT_DEGREE) {
            OutDegreeRangeFilter outDegreeRangeFilter = new OutDegreeRangeBuilder.OutDegreeRangeFilter();
            query = filterController.createQuery(outDegreeRangeFilter);
            outDegreeRangeFilter.setRange(range);
        } else {
            // Shouldn't happen ever
            throw Py.TypeError("Unexpected error");
        }

        return query;
    }

    @Override
    protected Query buildEqualsQuery(PyObject match) {
        Integer intMatch = (Integer) match.__tojava__(Integer.class);
        // FIXME: this is not working correctly
        return buildRangeQuery(new Range(intMatch, intMatch, Integer.MIN_VALUE, Integer.MAX_VALUE));
    }
}
