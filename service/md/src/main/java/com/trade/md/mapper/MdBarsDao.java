package com.trade.md.mapper;

import com.trade.md.model.dto.MdBarRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MdBarsDao {
    int upsertBars(@Param("bars") List<MdBarRow> bars);

    List<MdBarRow> selectBars(@Param("instrumentId") String instrumentId,
                              @Param("intervalCd") String intervalCd,
                              @Param("fromTs") String fromTs,
                              @Param("toTs") String toTs,
                              @Param("size") int size,
                              @Param("order") String order);

}
