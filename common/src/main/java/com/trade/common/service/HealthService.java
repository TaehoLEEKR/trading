package com.trade.common.service;

import com.trade.common.repository.health.DbHealthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final DbHealthRepository dbHealthRepository;

    public Boolean ping(){
        try{
            dbHealthRepository.ping();
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
