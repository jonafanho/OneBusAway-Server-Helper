package org.transport.service;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.BookingRule;
import org.transport.repository.BookingRuleRepository;

@Component
public final class BookingRuleProcessor extends GtfsStaticDataProcessorBase<BookingRule, BookingRuleRepository, BookingRule.BookingRuleDTO> {

	public BookingRuleProcessor(BookingRuleRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected BookingRule.BookingRuleDTO process(BookingRule.BookingRuleDTO data, int sourceIndex) {
		return data;
	}
}
