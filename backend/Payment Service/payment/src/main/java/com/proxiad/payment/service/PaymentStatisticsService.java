package com.proxiad.payment.service;

import com.proxiad.payment.entity.PaymentEntity;
import com.proxiad.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import com.proxiad.payment.enums.PaymentStatus;

@Service
public class PaymentStatisticsService {

    private final PaymentRepository paymentRepository;

    public PaymentStatisticsService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // ==================== METHODES POUR LES REVENUS ====================

    public Map<String, Object> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<PaymentEntity> payments = paymentRepository.findByCreatedAtBetweenAndStatus(
                startDateTime, endDateTime, PaymentStatus.CAPTURED);

        // Grouper par jour et calculer les revenus
        Map<LocalDate, BigDecimal> dailyRevenueMap = payments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getCreatedAt().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, (payment) -> payment.getAmount().getValue(), BigDecimal::add)
                ));

        BigDecimal totalRevenue = dailyRevenueMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> data = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("revenue", dailyRevenueMap.getOrDefault(date, BigDecimal.ZERO));
            data.add(dayData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("period", "daily");
        result.put("startDate", startDate.toString());
        result.put("endDate", endDate.toString());
        result.put("data", data);

        return result;
    }

    public Map<String, Object> getWeeklyRevenue(Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<PaymentEntity> payments = paymentRepository.findByCreatedAtBetweenAndStatus(
                startOfMonth, endOfMonth, PaymentStatus.CAPTURED);

        // Grouper par semaine
        Map<Integer, BigDecimal> weeklyRevenueMap = new HashMap<>();
        Map<Integer, LocalDate> weekStartMap = new HashMap<>();
        Map<Integer, LocalDate> weekEndMap = new HashMap<>();

        for (PaymentEntity payment : payments) {
            LocalDate paymentDate = payment.getCreatedAt().toLocalDate();
            int weekOfMonth = getWeekOfMonth(paymentDate);

            weeklyRevenueMap.put(weekOfMonth,
                    weeklyRevenueMap.getOrDefault(weekOfMonth, BigDecimal.ZERO).add(payment.getAmount().getValue()));

            // Calculer le début et la fin de semaine
            if (!weekStartMap.containsKey(weekOfMonth)) {
                LocalDate weekStart = paymentDate.with(DayOfWeek.MONDAY);
                LocalDate weekEnd = paymentDate.with(DayOfWeek.SUNDAY);
                weekStartMap.put(weekOfMonth, weekStart);
                weekEndMap.put(weekOfMonth, weekEnd);
            }
        }

        BigDecimal totalRevenue = weeklyRevenueMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> data = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : weeklyRevenueMap.entrySet()) {
            Map<String, Object> weekData = new HashMap<>();
            weekData.put("weekNumber", entry.getKey());
            weekData.put("revenue", entry.getValue());
            weekData.put("weekStart", weekStartMap.get(entry.getKey()).toString());
            weekData.put("weekEnd", weekEndMap.get(entry.getKey()).toString());
            data.add(weekData);
        }

        // Trier par numéro de semaine
        data.sort((a, b) -> Integer.compare((Integer) a.get("weekNumber"), (Integer) b.get("weekNumber")));

        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("period", "weekly");
        result.put("year", year);
        result.put("month", month);
        result.put("data", data);

        return result;
    }

    public Map<String, Object> getMonthlyRevenue(Integer year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        List<PaymentEntity> payments = paymentRepository.findByCreatedAtBetweenAndStatus(
                startOfYear, endOfYear, PaymentStatus.CAPTURED);

        // Grouper par mois
        Map<Integer, BigDecimal> monthlyRevenueMap = payments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getCreatedAt().getMonthValue(),
                        Collectors.reducing(BigDecimal.ZERO, (payment) -> payment.getAmount().getValue(), BigDecimal::add)
                ));

        BigDecimal totalRevenue = monthlyRevenueMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> data = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month);
            monthData.put("monthName", Month.of(month).getDisplayName(TextStyle.FULL, Locale.FRENCH));
            monthData.put("revenue", monthlyRevenueMap.getOrDefault(month, BigDecimal.ZERO));
            data.add(monthData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("period", "monthly");
        result.put("year", year);
        result.put("data", data);

        return result;
    }

    // ==================== METHODES POUR LE NOMBRE DE TRANSACTIONS ====================

    public Map<String, Object> getDailyTransactionCount(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<PaymentEntity> payments = paymentRepository.findByCreatedAtBetween(startDateTime, endDateTime);

        // Grouper par jour et compter
        Map<LocalDate, Long> dailyCountMap = payments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        Long totalTransactions = (long) payments.size();

        List<Map<String, Object>> data = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("count", dailyCountMap.getOrDefault(date, 0L));
            data.add(dayData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalTransactions", totalTransactions);
        result.put("period", "daily");
        result.put("startDate", startDate.toString());
        result.put("endDate", endDate.toString());
        result.put("data", data);

        return result;
    }

    public Map<String, Object> getMonthlyTransactionCount(Integer year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        List<PaymentEntity> payments = paymentRepository.findByCreatedAtBetween(startOfYear, endOfYear);

        // Grouper par mois et compter
        Map<Integer, Long> monthlyCountMap = payments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getCreatedAt().getMonthValue(),
                        Collectors.counting()
                ));

        Long totalTransactions = (long) payments.size();

        List<Map<String, Object>> data = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month);
            monthData.put("monthName", Month.of(month).getDisplayName(TextStyle.FULL, Locale.FRENCH));
            monthData.put("count", monthlyCountMap.getOrDefault(month, 0L));
            data.add(monthData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalTransactions", totalTransactions);
        result.put("period", "monthly");
        result.put("year", year);
        result.put("data", data);

        return result;
    }

    // ==================== METHODES POUR LES TENDANCES ====================

    public Map<String, Object> getRevenueMonthlyTrends(Integer duration) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(duration - 1).withDayOfMonth(1);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<PaymentEntity> payments = paymentRepository.findByCreatedAtBetweenAndStatus(
                startDateTime, endDateTime, PaymentStatus.CAPTURED);

        // Grouper par année-mois
        Map<String, BigDecimal> monthlyRevenueMap = payments.stream()
                .collect(Collectors.groupingBy(
                        payment -> {
                            LocalDate date = payment.getCreatedAt().toLocalDate();
                            return String.format("%d-%02d", date.getYear(), date.getMonthValue());
                        },
                        Collectors.reducing(BigDecimal.ZERO, (payment) -> payment.getAmount().getValue(), BigDecimal::add)
                ));

        List<Map<String, Object>> data = new ArrayList<>();
        BigDecimal previousValue = null;

        // Générer tous les mois dans la plage
        for (LocalDate date = startDate; date.isBefore(endDate.withDayOfMonth(1).plusMonths(1)); date = date.plusMonths(1)) {
            String period = String.format("%d-%02d", date.getYear(), date.getMonthValue());
            BigDecimal revenue = monthlyRevenueMap.getOrDefault(period, BigDecimal.ZERO);

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("period", period);
            monthData.put("year", date.getYear());
            monthData.put("month", date.getMonthValue());
            monthData.put("value", revenue);

            if (previousValue != null) {
                BigDecimal percentageChange = calculatePercentageChange(previousValue, revenue);
                monthData.put("changePercent", percentageChange);
                monthData.put("trend", percentageChange.compareTo(BigDecimal.ZERO) > 0 ? "increasing" :
                        percentageChange.compareTo(BigDecimal.ZERO) < 0 ? "decreasing" : "stable");
            }

            data.add(monthData);
            previousValue = revenue;
        }

        // Calcul de la tendance globale
        String overallTrend = "stable";
        if (data.size() > 1) {
            BigDecimal firstValue = (BigDecimal) data.get(0).get("value");
            BigDecimal lastValue = (BigDecimal) data.get(data.size() - 1).get("value");
            overallTrend = lastValue.compareTo(firstValue) > 0 ? "increasing" :
                    lastValue.compareTo(firstValue) < 0 ? "decreasing" : "stable";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("metric", "revenue");
        result.put("period", "monthly");
        result.put("duration", duration);
        result.put("overallTrend", overallTrend);
        result.put("data", data);

        return result;
    }

    public Map<String, Object> getRevenueWeeklyTrends(Integer duration) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(duration - 1);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<PaymentEntity> payments = paymentRepository.findByCreatedAtBetweenAndStatus(
                startDateTime, endDateTime, PaymentStatus.CAPTURED);

        // Grouper par année-semaine
        Map<String, BigDecimal> weeklyRevenueMap = payments.stream()
                .collect(Collectors.groupingBy(
                        payment -> {
                            LocalDate date = payment.getCreatedAt().toLocalDate();
                            int weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfYear());
                            return String.format("%d-W%02d", date.getYear(), weekOfYear);
                        },
                        Collectors.reducing(BigDecimal.ZERO, (payment) -> payment.getAmount().getValue(), BigDecimal::add)
                ));

        List<Map<String, Object>> data = new ArrayList<>();
        BigDecimal previousValue = null;

        // Générer toutes les semaines dans la plage
        for (LocalDate date = startDate; date.isBefore(endDate.plusWeeks(1)); date = date.plusWeeks(1)) {
            int weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfYear());
            String period = String.format("%d-W%02d", date.getYear(), weekOfYear);
            BigDecimal revenue = weeklyRevenueMap.getOrDefault(period, BigDecimal.ZERO);

            Map<String, Object> weekData = new HashMap<>();
            weekData.put("period", period);
            weekData.put("year", date.getYear());
            weekData.put("week", weekOfYear);
            weekData.put("weekStart", date.with(DayOfWeek.MONDAY).toString());
            weekData.put("value", revenue);

            if (previousValue != null) {
                BigDecimal percentageChange = calculatePercentageChange(previousValue, revenue);
                weekData.put("changePercent", percentageChange);
                weekData.put("trend", percentageChange.compareTo(BigDecimal.ZERO) > 0 ? "increasing" :
                        percentageChange.compareTo(BigDecimal.ZERO) < 0 ? "decreasing" : "stable");
            }

            data.add(weekData);
            previousValue = revenue;
        }

        // Calcul de la tendance globale
        String overallTrend = "stable";
        if (data.size() > 1) {
            BigDecimal firstValue = (BigDecimal) data.get(0).get("value");
            BigDecimal lastValue = (BigDecimal) data.get(data.size() - 1).get("value");
            overallTrend = lastValue.compareTo(firstValue) > 0 ? "increasing" :
                    lastValue.compareTo(firstValue) < 0 ? "decreasing" : "stable";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("metric", "revenue");
        result.put("period", "weekly");
        result.put("duration", duration);
        result.put("overallTrend", overallTrend);
        result.put("data", data);

        return result;
    }

    public Map<String, Object> getTransactionMonthlyTrends(Integer duration) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(duration - 1).withDayOfMonth(1);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<PaymentEntity> payments = paymentRepository.findByCreatedAtBetween(startDateTime, endDateTime);

        // Grouper par année-mois et compter
        Map<String, Long> monthlyCountMap = payments.stream()
                .collect(Collectors.groupingBy(
                        payment -> {
                            LocalDate date = payment.getCreatedAt().toLocalDate();
                            return String.format("%d-%02d", date.getYear(), date.getMonthValue());
                        },
                        Collectors.counting()
                ));

        List<Map<String, Object>> data = new ArrayList<>();
        Long previousValue = null;

        // Générer tous les mois dans la plage
        for (LocalDate date = startDate; date.isBefore(endDate.withDayOfMonth(1).plusMonths(1)); date = date.plusMonths(1)) {
            String period = String.format("%d-%02d", date.getYear(), date.getMonthValue());
            Long count = monthlyCountMap.getOrDefault(period, 0L);

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("period", period);
            monthData.put("year", date.getYear());
            monthData.put("month", date.getMonthValue());
            monthData.put("value", count);

            if (previousValue != null) {
                Double percentageChange = calculatePercentageChange(previousValue, count);
                monthData.put("changePercent", percentageChange);
                monthData.put("trend", percentageChange > 0 ? "increasing" :
                        percentageChange < 0 ? "decreasing" : "stable");
            }

            data.add(monthData);
            previousValue = count;
        }

        // Calcul de la tendance globale
        String overallTrend = "stable";
        if (data.size() > 1) {
            Long firstValue = (Long) data.get(0).get("value");
            Long lastValue = (Long) data.get(data.size() - 1).get("value");
            overallTrend = lastValue > firstValue ? "increasing" :
                    lastValue < firstValue ? "decreasing" : "stable";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("metric", "transactions");
        result.put("period", "monthly");
        result.put("duration", duration);
        result.put("overallTrend", overallTrend);
        result.put("data", data);

        return result;
    }

    public Map<String, Object> getTransactionWeeklyTrends(Integer duration) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(duration - 1);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<PaymentEntity> payments = paymentRepository.findByCreatedAtBetween(startDateTime, endDateTime);

        // Grouper par année-semaine et compter
        Map<String, Long> weeklyCountMap = payments.stream()
                .collect(Collectors.groupingBy(
                        payment -> {
                            LocalDate date = payment.getCreatedAt().toLocalDate();
                            int weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfYear());
                            return String.format("%d-W%02d", date.getYear(), weekOfYear);
                        },
                        Collectors.counting()
                ));

        List<Map<String, Object>> data = new ArrayList<>();
        Long previousValue = null;

        // Générer toutes les semaines dans la plage
        for (LocalDate date = startDate; date.isBefore(endDate.plusWeeks(1)); date = date.plusWeeks(1)) {
            int weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfYear());
            String period = String.format("%d-W%02d", date.getYear(), weekOfYear);
            Long count = weeklyCountMap.getOrDefault(period, 0L);

            Map<String, Object> weekData = new HashMap<>();
            weekData.put("period", period);
            weekData.put("year", date.getYear());
            weekData.put("week", weekOfYear);
            weekData.put("weekStart", date.with(DayOfWeek.MONDAY).toString());
            weekData.put("value", count);

            if (previousValue != null) {
                Double percentageChange = calculatePercentageChange(previousValue, count);
                weekData.put("changePercent", percentageChange);
                weekData.put("trend", percentageChange > 0 ? "increasing" :
                        percentageChange < 0 ? "decreasing" : "stable");
            }

            data.add(weekData);
            previousValue = count;
        }

        // Calcul de la tendance globale
        String overallTrend = "stable";
        if (data.size() > 1) {
            Long firstValue = (Long) data.get(0).get("value");
            Long lastValue = (Long) data.get(data.size() - 1).get("value");
            overallTrend = lastValue > firstValue ? "increasing" :
                    lastValue < firstValue ? "decreasing" : "stable";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("metric", "transactions");
        result.put("period", "weekly");
        result.put("duration", duration);
        result.put("overallTrend", overallTrend);
        result.put("data", data);

        return result;
    }

    // ==================== METHODES POUR LES TAUX DE REUSSITE ====================

    public Map<String, Object> getMonthlySuccessRate(Integer year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        List<PaymentEntity> allPayments = paymentRepository.findByCreatedAtBetween(startOfYear, endOfYear);
        List<PaymentEntity> successfulPayments = paymentRepository.findByCreatedAtBetweenAndStatus(
                startOfYear, endOfYear, PaymentStatus.CAPTURED);

        // Grouper tous les paiements par mois
        Map<Integer, Long> totalByMonth = allPayments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getCreatedAt().getMonthValue(),
                        Collectors.counting()
                ));

        // Grouper les paiements réussis par mois
        Map<Integer, Long> successfulByMonth = successfulPayments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getCreatedAt().getMonthValue(),
                        Collectors.counting()
                ));

        Long totalTransactions = (long) allPayments.size();
        Long totalSuccessful = (long) successfulPayments.size();

        List<Map<String, Object>> data = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Long monthlyTotal = totalByMonth.getOrDefault(month, 0L);
            Long monthlySuccessful = successfulByMonth.getOrDefault(month, 0L);

            Double successRate = monthlyTotal > 0 ?
                    (monthlySuccessful.doubleValue() / monthlyTotal.doubleValue()) * 100 : 0.0;

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month);
            monthData.put("monthName", Month.of(month).getDisplayName(TextStyle.FULL, Locale.FRENCH));
            monthData.put("successfulTransactions", monthlySuccessful);
            monthData.put("totalTransactions", monthlyTotal);
            monthData.put("successRate", Math.round(successRate * 100.0) / 100.0);
            data.add(monthData);
        }

        Double overallSuccessRate = totalTransactions > 0 ?
                (totalSuccessful.doubleValue() / totalTransactions.doubleValue()) * 100 : 0.0;

        Map<String, Object> result = new HashMap<>();
        result.put("overallSuccessRate", Math.round(overallSuccessRate * 100.0) / 100.0);
        result.put("totalTransactions", totalTransactions);
        result.put("successfulTransactions", totalSuccessful);
        result.put("period", "monthly");
        result.put("year", year);
        result.put("data", data);

        return result;
    }

    public Map<String, Object> getSuccessRateByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<PaymentEntity> allPayments = paymentRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        List<PaymentEntity> successfulPayments = paymentRepository.findByCreatedAtBetweenAndStatus(
                startDateTime, endDateTime, PaymentStatus.CAPTURED);

        Long totalTransactions = (long) allPayments.size();
        Long successfulTransactions = (long) successfulPayments.size();

        Double overallSuccessRate = totalTransactions > 0 ?
                (successfulTransactions.doubleValue() / totalTransactions.doubleValue()) * 100 : 0.0;

        // Grouper par jour pour les données détaillées
        Map<LocalDate, Long> totalByDay = allPayments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        Map<LocalDate, Long> successfulByDay = successfulPayments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> data = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Long dailyTotal = totalByDay.getOrDefault(date, 0L);
            Long dailySuccessful = successfulByDay.getOrDefault(date, 0L);

            Double dailySuccessRate = dailyTotal > 0 ?
                    (dailySuccessful.doubleValue() / dailyTotal.doubleValue()) * 100 : 0.0;

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("successfulTransactions", dailySuccessful);
            dayData.put("totalTransactions", dailyTotal);
            dayData.put("successRate", Math.round(dailySuccessRate * 100.0) / 100.0);
            data.add(dayData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("overallSuccessRate", Math.round(overallSuccessRate * 100.0) / 100.0);
        result.put("totalTransactions", totalTransactions);
        result.put("successfulTransactions", successfulTransactions);
        result.put("startDate", startDate.toString());
        result.put("endDate", endDate.toString());
        result.put("data", data);

        return result;
    }

    // ==================== METHODES UTILITAIRES ====================

    private BigDecimal calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return newValue.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal("100");
        }

        BigDecimal change = newValue.subtract(oldValue);
        BigDecimal percentageChange = change.divide(oldValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        return percentageChange.setScale(2, RoundingMode.HALF_UP);
    }

    private Double calculatePercentageChange(Long oldValue, Long newValue) {
        if (oldValue == 0) {
            return newValue == 0 ? 0.0 : 100.0;
        }

        double change = newValue - oldValue;
        double percentageChange = (change / oldValue) * 100;

        return Math.round(percentageChange * 100.0) / 100.0;
    }

    private int getWeekOfMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        int dayOfMonth = date.getDayOfMonth();
        int firstWeekDay = firstDayOfMonth.getDayOfWeek().getValue();

        return (dayOfMonth + firstWeekDay - 2) / 7 + 1;
    }
}