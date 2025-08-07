package org.transport.processor;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.BookingRule;
import org.transport.repository.BookingRuleRepository;
import org.transport.type.DTOBase;

@Component
public final class BookingRuleProcessor extends GtfsStaticDataProcessorBase<BookingRule, BookingRuleRepository, BookingRule.BookingRuleDTO> {

	public BookingRuleProcessor(BookingRuleRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected BookingRule.BookingRuleDTO process(BookingRule.BookingRuleDTO data, int sourceIndex) {
		data.priorNoticeServiceId = DTOBase.convertId(sourceIndex, data.priorNoticeServiceId);
		return data;
	}
}
