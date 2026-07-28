//// Review: https://claude.ai/chat/744f30fc-6b72-4882-b54b-59cee47e27e1
//
//package com.proxiad.payment.controller;
//
//import service.com.abdelkhalek.storehub.payment.PaymentStatisticsService;
//import jakarta.validation.constraints.Max;
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDate;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/payments/statistics")
//@Validated
//public class PaymentStatisticsController {
//
//    private final PaymentStatisticsService paymentStatisticsService;
//
//    public PaymentStatisticsController(PaymentStatisticsService paymentStatisticsService) {
//        this.paymentStatisticsService = paymentStatisticsService;
//    }
//
//    // Income Per Period
//    @GetMapping("/revenue")
//    public ResponseEntity<Map<String, Object>> getRevenue(
//            @RequestParam @NotBlank String period,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
//            @RequestParam(required = false) Integer year,
//            @RequestParam(required = false) @Min(1) @Max(12) Integer month) {
//
//        return ResponseEntity.ok().body(Map.of("response","""
//                {
//                  "totalRevenue": 15000.50,
//                  "period": "daily",
//                  "data": [
//                    {"date": "2024-01-01", "revenue": 500.00},
//                    {"date": "2024-01-02", "revenue": 750.25}
//                  ]
//                }
//                """));
////        try {
////            // Validation de la période
////            PeriodType periodType = PeriodType.valueOf(period.toUpperCase());
////
////            Map<String, Object> revenueData;
////
////            switch (periodType) {
////                case DAILY:
////                    if (startDate == null || endDate == null) {
////                        return ResponseEntity.badRequest()
////                                .body(Map.of("error", "start_date and end_date are required for daily period"));
////                    }
////                    if (startDate.isAfter(endDate)) {
////                        return ResponseEntity.badRequest()
////                                .body(Map.of("error", "start_date must be before end_date"));
////                    }
////                    revenueData = paymentStatisticsService.getDailyRevenue(startDate, endDate);
////                    break;
////
////                case WEEKLY:
////                    if (year == null || month == null) {
////                        return ResponseEntity.badRequest()
////                                .body(Map.of("error", "year and month are required for weekly period"));
////                    }
////                    revenueData = paymentStatisticsService.getWeeklyRevenue(year, month);
////                    break;
////
////                case MONTHLY:
////                    if (year == null) {
////                        return ResponseEntity.badRequest()
////                                .body(Map.of("error", "year is required for monthly period"));
////                    }
////                    revenueData = paymentStatisticsService.getMonthlyRevenue(year);
////                    break;
////
////                default:
////                    return ResponseEntity.badRequest()
////                            .body(Map.of("error", "Invalid period. Allowed values: daily, weekly, monthly"));
////            }
////
////            return ResponseEntity.ok(revenueData);
////
////        } catch (IllegalArgumentException e) {
////            return ResponseEntity.badRequest()
////                    .body(Map.of("error", "Invalid period value: " + period));
////        } catch (Exception e) {
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                    .body(Map.of("error", "An error occurred while fetching revenue data"));
////        }
//    }
//
//    // Transaction Count
//    @GetMapping("/transactions-count")
//    public ResponseEntity<Map<String, Object>> getTransactionsCount(
//            @RequestParam @NotBlank String period,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
//            @RequestParam(required = false) Integer year) {
//        return ResponseEntity.ok().body(Map.of("response","""
//                {
//                  "totalTransactions": 150,
//                  "period": "monthly",\s
//                  "data": [
//                    {"month": "January", "count": 45},
//                    {"month": "February", "count": 52}
//                  ]
//                }
//                """));
////        try {
////            PeriodType periodType = PeriodType.valueOf(period.toUpperCase());
////
////            Map<String, Object> transactionData;
////
////            switch (periodType) {
////                case DAILY:
////                    if (startDate == null || endDate == null) {
////                        return ResponseEntity.badRequest()
////                                .body(Map.of("error", "start_date and end_date are required for daily period"));
////                    }
////                    if (startDate.isAfter(endDate)) {
////                        return ResponseEntity.badRequest()
////                                .body(Map.of("error", "start_date must be before end_date"));
////                    }
////                    transactionData = paymentStatisticsService.getDailyTransactionCount(startDate, endDate);
////                    break;
////
////                case MONTHLY:
////                    if (year == null) {
////                        return ResponseEntity.badRequest()
////                                .body(Map.of("error", "year is required for monthly period"));
////                    }
////                    transactionData = paymentStatisticsService.getMonthlyTransactionCount(year);
////                    break;
////
////                default:
////                    return ResponseEntity.badRequest()
////                            .body(Map.of("error", "Invalid period. Allowed values for transactions count: daily, monthly"));
////            }
////
////            return ResponseEntity.ok(transactionData);
////
////        } catch (IllegalArgumentException e) {
////            return ResponseEntity.badRequest()
////                    .body(Map.of("error", "Invalid period value: " + period));
////        } catch (Exception e) {
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                    .body(Map.of("error", "An error occurred while fetching transaction count data"));
////        }
//    }
//
//    @GetMapping("/trends")
//    public ResponseEntity<Map<String, Object>> getTrends(
//            @RequestParam @NotBlank String metric,
//            @RequestParam @NotBlank String period,
//            @RequestParam @Min(1) @Max(24) Integer duration) {
//        return ResponseEntity.ok().body(Map.of("response","""
//                {
//                   "metric": "revenue",
//                   "period": "monthly",
//                   "duration": 12,
//                   "trend": "increasing",
//                   "data": [
//                     {"period": "2023-01", "value": 1000.00},
//                     {"period": "2023-02", "value": 1200.00}
//                   ]
//                 }
//                """));
////        try {
////            MetricType metricType = MetricType.valueOf(metric.toUpperCase());
////            PeriodType periodType = PeriodType.valueOf(period.toUpperCase());
////
////            Map<String, Object> trendsData;
////
////            if (metricType == MetricType.REVENUE) {
////                if (periodType == PeriodType.MONTHLY) {
////                    trendsData = paymentStatisticsService.getRevenueMonthlyTrends(duration);
////                } else if (periodType == PeriodType.WEEKLY) {
////                    trendsData = paymentStatisticsService.getRevenueWeeklyTrends(duration);
////                } else {
////                    return ResponseEntity.badRequest()
////                            .body(Map.of("error", "Invalid period for revenue trends. Allowed values: monthly, weekly"));
////                }
////            } else if (metricType == MetricType.TRANSACTIONS) {
////                if (periodType == PeriodType.MONTHLY) {
////                    trendsData = paymentStatisticsService.getTransactionMonthlyTrends(duration);
////                } else if (periodType == PeriodType.WEEKLY) {
////                    trendsData = paymentStatisticsService.getTransactionWeeklyTrends(duration);
////                } else {
////                    return ResponseEntity.badRequest()
////                            .body(Map.of("error", "Invalid period for transaction trends. Allowed values: monthly, weekly"));
////                }
////            } else {
////                return ResponseEntity.badRequest()
////                        .body(Map.of("error", "Invalid metric. Allowed values: revenue, transactions"));
////            }
////
////            return ResponseEntity.ok(trendsData);
////
////        } catch (IllegalArgumentException e) {
////            return ResponseEntity.badRequest()
////                    .body(Map.of("error", "Invalid parameter value"));
////        } catch (Exception e) {
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                    .body(Map.of("error", "An error occurred while fetching trends data"));
////        }
//    }
//
//    @GetMapping("/success-rate")
//    public ResponseEntity<Map<String, Object>> getSuccessRate(
//            @RequestParam(required = false) String period,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
//            @RequestParam(required = false) Integer year) {
//        return ResponseEntity.ok().body(Map.of("response","""
//                {
//                  "overallSuccessRate": 95.5,
//                  "totalTransactions": 1000,
//                  "successfulTransactions": 955,
//                  "data": [
//                    {"period": "January", "successRate": 94.2},
//                    {"period": "February", "successRate": 96.8}
//                  ]
//                }
//                """));
////        try {
////            Map<String, Object> successRateData;
////
////            // Si period est fourni, on utilise la logique par période
////            if (period != null && !period.isEmpty()) {
////                PeriodType periodType = PeriodType.valueOf(period.toUpperCase());
////
////                if (periodType == PeriodType.MONTHLY) {
////                    if (year == null) {
////                        return ResponseEntity.badRequest()
////                                .body(Map.of("error", "year is required for monthly period"));
////                    }
////                    successRateData = paymentStatisticsService.getMonthlySuccessRate(year);
////                } else {
////                    return ResponseEntity.badRequest()
////                            .body(Map.of("error", "Invalid period for success rate. Allowed values: monthly"));
////                }
////            } else {
////                // Sinon on utilise la plage de dates
////                if (startDate == null || endDate == null) {
////                    return ResponseEntity.badRequest()
////                            .body(Map.of("error", "Either period with year OR start_date with end_date must be provided"));
////                }
////                if (startDate.isAfter(endDate)) {
////                    return ResponseEntity.badRequest()
////                            .body(Map.of("error", "start_date must be before end_date"));
////                }
////                successRateData = paymentStatisticsService.getSuccessRateByDateRange(startDate, endDate);
////            }
////
////            return ResponseEntity.ok(successRateData);
////
////        } catch (IllegalArgumentException e) {
////            return ResponseEntity.badRequest()
////                    .body(Map.of("error", "Invalid parameter value"));
////        } catch (Exception e) {
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                    .body(Map.of("error", "An error occurred while fetching success rate data"));
////        }
//    }
//
//    // Enums pour la validation
//    public enum PeriodType {
//        DAILY, WEEKLY, MONTHLY, YEARLY
//    }
//
//    public enum MetricType {
//        REVENUE, TRANSACTIONS
//    }
//}