package com.tu.wahlinfo.csv.entities;

import java.util.Map;

public interface Persistable {
    
    /**
     * Transforms the object's data to a relational struct. Member names thereby serve as map-key and their value as map-value.
     * @return The resulting map.
     */
    public Map<String,String> toRelationalStruct();

}
