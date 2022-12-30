package com.example.stock.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestScheduler {

    @Scheduled(cron = "0/5 * * * * *")
    public void test(){
        System.out.println("now -> " + Thread.currentThread() + ": " +  System.currentTimeMillis());
    }

    // 일정 주기마다 수행
    @Scheduled(cron = "")
    public void yahooFinanceScheduling() {
        // 저장된 회사 목록을 조회

        // 회사마다 배당금 정보를 새로 스크래핑

        // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
    }
}
