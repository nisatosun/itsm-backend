# Workflow Engine Architectural Decision

## Context
The ITSM backend requires a strict state machine to govern ticket lifecycles (`NEW -> TRIAGE -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED`).

## Why Custom Workflow Engine is Used Now
In this initial release, we implemented an in-house Java-based workflow engine (`WorkflowEngineServiceImpl`) rather than an external BPM suite.
- **Simplicity:** It avoids the massive infrastructure overhead of running a full BPM server during early iterative development.
- **Velocity:** It allowed us to ship the MVP and core ITSM functionalities quickly without fighting BPMN semantics.

## Why jBPM/BPMN is Deferred
Red Hat jBPM is a heavyweight enterprise engine. Integrating it requires managing an external process server or embedding a complex rule engine. It has been deferred to v2.0 when cross-departmental, complex, and dynamic routing rules are required.

## What is Already Abstracted
We specifically designed the architecture to be **"BPM-ready"**.
- The `WorkflowService` interface serves as the entry point.
- Tickets store a `processInstanceId`, a direct nod to standard BPM engines.

### Key Roles
- **WorkflowEngineService:** The core orchestrator that executes transitions.
- **WorkflowTransitionPolicy:** The rule validator ensuring transitions are logically sound (e.g., cannot jump from `NEW` to `CLOSED`).
- **WorkflowAssignmentPolicy:** Validates if the requesting user holds the correct permissions and roles to execute a specific transition.

## ProcessInstanceId Strategy
Currently, `processInstanceId` generates a UUID tracking the internal state machine session. When jBPM is introduced, this field will map directly to the jBPM container's `processInstanceId`.

## Future Migration Path to jBPM
1. Add `jbpm-spring-boot-starter`.
2. Replace `WorkflowEngineServiceImpl` with a `JbpmWorkflowEngineImpl`.
3. Design the `.bpmn2` file mapping the exact same state machine.
4. No endpoints, controllers, or frontend systems will need to change because the abstraction boundary (`WorkflowService`) remains identical.
