package com.example.nagoyameshi.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceCollection;

@Service
public class StripeService {

	@Value("${stripe.api-key}")
	private String stripeApiKey;

	/**
	 * 月間売上を取得する
	 * 
	 * @param startOfMonth 
	 * @return
	 * @throws StripeException 
	 */
	public long getMonthlyTotalRevenue(LocalDate startOfMonth) throws StripeException {
		Stripe.apiKey = stripeApiKey;

		Instant firstDayOfMonthInstant = startOfMonth.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault())
				.toInstant();
		Instant lastDayOfMonthInstant = startOfMonth.plusMonths(1).withDayOfMonth(1)
				.atStartOfDay(ZoneId.systemDefault()).toInstant();

		long startTime = firstDayOfMonthInstant.getEpochSecond();
		long endTime = lastDayOfMonthInstant.getEpochSecond();

		Map<String, Object> params = new HashMap<>();
		params.put("created", Map.of(
				"gte", startTime,
				"lt", endTime));

		InvoiceCollection invoices = Invoice.list(params);

		long totalRevenueCents = 0;

		for (Invoice invoice : invoices.getData()) {
			totalRevenueCents += invoice.getAmountPaid();
		}

		return totalRevenueCents;
	}

}
