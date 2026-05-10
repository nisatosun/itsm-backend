# Schema Quality & Integrity Checklist

This document ensures the database schema maintains strict relational integrity, optimization, and consistency.

## 1. Foreign Key Integrity Checks
Ensure no orphaned child records exist and FK constraints are strictly enforced.

```sql
-- Check for orphan tickets without a valid assignee (when assignee_id is NOT NULL)
SELECT t.id, t.assignee_id FROM tickets t 
LEFT JOIN users u ON t.assignee_id = u.id 
WHERE t.assignee_id IS NOT NULL AND u.id IS NULL;
```

## 2. Orphan Row Checks
Ensure sub-resources do not outlive their parent tickets.

```sql
-- Check for comments with missing tickets
SELECT c.id FROM comments c 
LEFT JOIN tickets t ON c.ticket_id = t.id 
WHERE t.id IS NULL;
```

## 3. Index Checks
Verify that all frequently queried columns possess B-Tree indices.

```sql
-- Verify indexes exist for critical reporting columns
SELECT indexname, indexdef 
FROM pg_indexes 
WHERE tablename IN ('tickets', 'worklogs');
-- Expected: indexes on tickets(status), tickets(assignee_id), worklogs(user_id)
```

## 4. Nullable/Default Checks
Ensure critical system fields possess deterministic defaults.

```sql
-- Verify boolean flags do not allow NULL (Three-value logic prevention)
SELECT column_name, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'sla_tracking' AND column_name = 'breached';
-- Expected: is_nullable = 'NO', column_default = 'false'
```

## 5. Audit Consistency Checks
Ensure audit logs immutably link to valid actions.

```sql
-- Verify audit actions
SELECT action, count(*) FROM audit_logs GROUP BY action;
```

## 6. Workflow History Checks
Verify workflow history correctly chains state transitions without gaps.

```sql
-- Verify sequential integrity per ticket
SELECT ticket_id, from_status, to_status, created_at 
FROM workflow_history 
ORDER BY ticket_id, created_at ASC;
```

## 7. SLA Tracking Checks
Verify SLA records do not have inverted timestamps.

```sql
-- Ensure due dates are ALWAYS after start dates
SELECT id, ticket_id FROM sla_tracking WHERE due_date <= start_time;
-- Expected: 0 rows
```

## 8. Attachments/Comments/Worklogs Checks
Validate content integrity constraints.

```sql
-- Ensure no empty comments
SELECT id FROM comments WHERE content IS NULL OR TRIM(content) = '';

-- Ensure no 0-minute or negative worklogs
SELECT id FROM worklogs WHERE time_spent_minutes <= 0;
```
