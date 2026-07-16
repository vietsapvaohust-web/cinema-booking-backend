package com.cinema.booking.domain.strategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class PaymentStrategyFactory {

    private final Map<String, PaymentStrategy> strategies;

    // Spring tự động tiêm TOÀN BỘ bean implement PaymentStrategy vào đây (nhờ @Component ở mỗi class),
    // sau đó ta gom lại thành Map để tra cứu theo tên cho tiện - không cần if-else dài dòng
    public PaymentStrategyFactory(List<PaymentStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(PaymentStrategy::getMethodName, s -> s));
    }

    public PaymentStrategy getStrategy(String methodName) {
        PaymentStrategy strategy = strategies.get(methodName.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Phương thức thanh toán không được hỗ trợ: " + methodName);
        }
        return strategy;
    }
}