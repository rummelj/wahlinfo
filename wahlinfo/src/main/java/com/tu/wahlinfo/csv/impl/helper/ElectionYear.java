package com.tu.wahlinfo.csv.impl.helper;

/**
 *
 * @author cg
 */
public enum ElectionYear {

    _2005, _2009;

    public String toCleanString() {
        if (this.equals(ElectionYear._2005)) {
            return Integer.toString(2005);
        } else {
            return Integer.toString(200);
        }
    }
}
