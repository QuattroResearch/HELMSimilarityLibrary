/*******************************************************************************
 * Copyright C 2016, QUATTRO RESEARCH GMBH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package org.pistoiaalliance.helm.HELMSimilarityLibrary.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.helm.notation2.parser.notation.polymer.MonomerNotation;

/**
 * Vertex class is a data model for a vertex in a Graph. It is based on the
 * monomer notation.
 *
 * @author bueltel
 */
public class Vertex {

    private MonomerNotation monomer;
    private List<Vertex> neighbourList;
    private boolean isVisited;

    public enum Type {
        PEPTIDE, RNA, CHEM
    }

    public Type monomerType;
    private boolean isNonNatural;
    private boolean hasUniqueUnit;
    private Vertex parent;
    private int index;

    public Vertex() {
        this.neighbourList = new LinkedList<>();
    }

    public Vertex(Vertex vertex) {
        this.monomer = vertex.getMonomer();
        this.neighbourList = vertex.getNeighbourList();
        this.isVisited = vertex.isVisited;
        this.isNonNatural = vertex.isNonNatural;
        this.hasUniqueUnit = vertex.hasUniqueUnit;
        this.parent = vertex.parent;
        this.index = vertex.index;
        this.monomerType = vertex.monomerType;
    }

    public Vertex(Vertex vertex, Type monomerType) {
        this.monomer = vertex.getMonomer();
        this.neighbourList = vertex.getNeighbourList();
        this.isVisited = vertex.isVisited;
        this.isNonNatural = vertex.isNonNatural;
        this.hasUniqueUnit = vertex.hasUniqueUnit;
        this.parent = vertex.parent;
        this.index = vertex.index;
        this.monomerType = monomerType;
    }

    public Vertex(MonomerNotation monomer) {
        this.monomer = monomer;
        this.neighbourList = new LinkedList<>();
    }

    public Vertex(MonomerNotation monomer, Type monomerType, boolean isNonNatural,
                  boolean hasUniqueUnit, int index) {
        super();
        this.monomer = monomer;
        this.isNonNatural = isNonNatural;
        this.index = index;
        this.monomerType = monomerType;
        this.neighbourList = new LinkedList<>();
        this.hasUniqueUnit = hasUniqueUnit;
    }

    public MonomerNotation getMonomer() {
        return this.monomer;
    }

    public void addNeighbour(Vertex vertex) {
        this.neighbourList.add(vertex);
    }

    public void addBothNeighbours(Vertex vertex) {
        this.neighbourList.add(vertex);
        vertex.neighbourList.add(this);
    }

    public boolean isVisited() {
        return this.isVisited;
    }

    public void setVisited(boolean visited) {
        this.isVisited = visited;
    }

    public void setMonomer(MonomerNotation monomer) {
        this.monomer = monomer;
    }

    public void setIndex(int idx) {
        this.index = idx;
    }

    public int getIndex() {
        return this.index;
    }

    public List<Vertex> getNeighbourList() {
        return neighbourList;
    }

    public void setNeighbourList(List<Vertex> neighbourList) {
        this.neighbourList = neighbourList;
    }

    public Vertex getParent() {
        return this.parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    public boolean isPeptide() {
        return monomerType == Type.PEPTIDE;
    }

    public boolean isRNA() {
        return monomerType == Type.RNA;
    }

    public boolean isChem() {
        return monomerType == Type.CHEM;
    }

    public boolean isNonNatural() {
        return this.isNonNatural;
    }

    public void setNonNatural() {
        this.isNonNatural = true;
    }

    public boolean hasUniqueUnit() {
        return this.hasUniqueUnit;
    }

    public void setHasUniqueUnit(boolean hasUniqueUnit) {
        this.hasUniqueUnit = hasUniqueUnit;
    }

    @Override
    public String toString() {
        return this.monomer.getUnit();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex otherVertex = (Vertex) o;
        if (this.isRNA() != otherVertex.isRNA()) {
            return false;
        }
        if (this.isPeptide() != otherVertex.isPeptide()) {
            return false;
        }
        if (this.isChem() != otherVertex.isChem()) {
            return false;
        }
        if (this.isNonNatural != otherVertex.isNonNatural) {
            return false;
        }
        return monomerEqual(otherVertex.monomer);
    }

    private boolean monomerEqual(MonomerNotation monomer2) {
        if (!this.monomer.getUnit().equals(monomer2.getUnit())) {
            return false;
        }
        if (!this.monomer.getCount().equals(monomer2.getCount())) {
            return false;
        }
        return this.monomer.getType().equals(monomer2.getType());
    }

}