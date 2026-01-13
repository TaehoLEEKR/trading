package com.trade.md.mapper;

import com.trade.md.model.dto.MdBarRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MdBarsDao {
    int upsertBars(@Param("bars") List<MdBarRow> bars);
}
