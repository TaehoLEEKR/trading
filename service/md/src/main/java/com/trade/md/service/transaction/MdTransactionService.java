package com.trade.md.service.transaction;

import com.trade.md.mapper.MdBarsDao;
import com.trade.md.model.dto.MdBarRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MdTransactionService {
    private final MdBarsDao mdBarsDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int upsertBarsInNewTx(List<MdBarRow> bars) {
        if (bars == null || bars.isEmpty()) return 0;
        mdBarsDao.upsertBars(bars);
        return bars.size();
    }
}
