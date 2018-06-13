package com.acuo.algo;

import org.renjin.invoke.reflection.converters.Converter;
import org.renjin.invoke.reflection.converters.RuntimeConverter;
import org.renjin.primitives.vector.RowNamesVector;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.Null;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringArrayVector;
import org.renjin.sexp.Symbols;

import java.util.Map;

public class MapConverter {

  private Converter<Object> elementConverter = RuntimeConverter.INSTANCE;

  public SEXP convertToR(Map<?,?> map, long rows) {
    ListVector.NamedBuilder list = new ListVector.NamedBuilder();
    list.setAttribute(Symbols.CLASS, new StringArrayVector("data.frame"));
    list.setAttribute(Symbols.ROW_NAMES, new RowNamesVector(Long.valueOf(rows).intValue()));
    for(Map.Entry<?,?> entry : map.entrySet()) {
      String key = entry.getKey().toString();
      SEXP value = entry.getValue() != null ? elementConverter.convertToR(entry.getValue()) : Null.INSTANCE;
      list.add(key, value);
    }
    return list.build();
  }
}
