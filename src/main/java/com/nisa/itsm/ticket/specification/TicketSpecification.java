package com.nisa.itsm.ticket.specification;

import com.nisa.itsm.common.enums.Priority;
import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.ticket.entity.Ticket;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class TicketSpecification {

        public static Specification<Ticket> hasStatus(TicketStatus status) {
                return (root, query, criteriaBuilder) -> status == null
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.equal(root.get("status"), status);
        }

        public static Specification<Ticket> hasPriority(Priority priority) {
                return (root, query, criteriaBuilder) -> priority == null
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.equal(root.get("priority"), priority);
        }

        public static Specification<Ticket> hasCategoryId(Long categoryId) {
                return (root, query, criteriaBuilder) -> categoryId == null
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        }

        public static Specification<Ticket> hasAssigneeId(Long assigneeId) {
                return (root, query, criteriaBuilder) -> assigneeId == null
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
        }

        public static Specification<Ticket> hasRequesterId(Long requesterId) {
                return (root, query, criteriaBuilder) -> requesterId == null
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.equal(root.get("requester").get("id"), requesterId);
        }

        public static Specification<Ticket> hasSlaBreached(Boolean slaBreached) {
                return (root, query, criteriaBuilder) -> slaBreached == null
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.equal(root.get("slaTracking").get("breached"), slaBreached);
        }

        public static Specification<Ticket> createdAfter(LocalDateTime createdAfter) {
                return (root, query, criteriaBuilder) -> createdAfter == null
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdAfter);
        }

        public static Specification<Ticket> createdBefore(LocalDateTime createdBefore) {
                return (root, query, criteriaBuilder) -> createdBefore == null
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdBefore);
        }

        public static Specification<Ticket> searchByKeyword(String search) {
                return (root, query, criteriaBuilder) -> {
                        if (search == null || search.trim().isEmpty()) {
                                return criteriaBuilder.conjunction();
                        }

                        String keyword = "%" + search.toLowerCase() + "%";

                        return criteriaBuilder.or(
                                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), keyword),
                                        criteriaBuilder.like(criteriaBuilder.lower(root.get("ticketNo")), keyword));
                };
        }
}
