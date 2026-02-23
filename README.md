LOGISTICS OPERATIONS IMPROVEMENT SYSTEM

This repository contains a set of design improvements applied to a logistics operations system. It is not a framework showcase nor an algorithmic demo. Its goal is to model real and recurring operational problems and to propose technical solutions with explicit and conscious trade-offs.
The core premise is simple:

In real systems, the problem is rarely "the code". The problem is usually and invalid system state caused by physical, temporal or operational constraints that the original model did not represent explicitly.

Each mini-project introduces a local change to the domain model in order to make the system more honest, predictable, and explainable.

MINI-PROJECT 1: TIME-AWARE PICKING WINDOW PLANNING

Real-World Problem:

In picking operations with overlapping time windows, the system tends to optimize local throughput instead of real temporal urgency. As a consequence:

- Critical windows are delivered late even when "nobody did anything wrong".
- Operations teams reprioritize manually outside the system.
- The system loses credibility as a source of truth.
- Some windows fall into batch without being picked and require manual workarounds.

The original model did not explicitly represent temporal urgency nor the operational failures derived from time constraints.

What this model change introduces:

This mini-project introduces explicit time semantics into the domain through operational window states:
- OPEN: >= 3 hours to deadline. Still open to new orders, not planned yet.
- CONFIRMED: < 3 hours and >= 30 minutes. Closed to changes, can be planned and can be mixed.
- URGENT: < 30 minutes. Can be picked, but must not be mixed with any other window.
- MISSING: an operational state (not a time state). Represents a system failure:
    - The window passed its deadline without being picked.
    - Or it was impacted by a confirmed order cancellation and requires a manual/return flow.

 Time is no longer just a date comparison; it becomes part of the domain state.

 Window mixing rules:

 Picking windows are allowed to be mixed according to explicit rules:
 - If either window is URGENT → mixing is not allowed.
 - Only windows in CONFIRMED state can be mixed.
 - Mixing is only allowed if both windows share at least one product.

This reflects a real operational constraint: mixing is acceptable only when there is real work overlap and no critical window is put at risk.

Why the MISSING state exists:

MISSING does not represent "time". It represents and explicit operational failure:
- A window that reaches batch without being picked is not just "late": it requires manual intervention (staging, reprocessing, etc.).
- A confirmed order cancellation implies a return or manual handling flow.

Instead of hiding these cases behind generic errors or exceptions, the system models them as a domain state, making them visible, traceable and actionable.

Trade-offs:

Costs:
- Lower local efficiency in some scenarios.
- More domain rules to maintain.

Benefits:
- More predictable SLA behavior.
- Less operational firefighting and fewer out-of-system workarounds.
- A more explainable and trustworthy system.
- Operational failures stop being implicit and become part of the model.

Out of Scope:

This mini-project does not address:
- Bag capacity.
- Picking area limits.
- Batch/orchestation implementation.
- UI or presentation layers.

Its only goal is to fix the temporal and state model of picking windows.

Where this lives in the code:

This behavior is modeled mainly in:
- PickingWindowStatus.
- PickingWindow.
- Product.

MINI-PROJECT 2: BAG QR RECYCLING (RESILIENT FLOW)

Real-World problem:

In real picking operations, bags can become unusable for many reasons: they can break, get wet, be the wrong size or be damaged during handling. When this happens, many systems either:
- Interrupt picking flow.
- Force manual workarounds outside the system.
- Lose traceability of what happened.
- Or penalize the operator for making the correct decision (discarding a bad bag).

The core issue is that the system usually models this as an exception or tries to undo previous steps, instead of treating it as a normal and expected operational failure mode.

What this model change introduces:

This mini-project introduces an explicit operational status:
- OPEN: the bag is ccurrently being filled.
- CLOSED: the bag was successfully closed and is ready for downstream processing.
- DISCARDED: the bag became unusable and must not be used anymore.
- REUSED: a new bag was created as a replacement for a discarded one (compensation).

Instead of trying to "rollback" the system, the model introduces a compensating action:
- The current bag is marked as DISCARDED.
- A new bag is created as a replacement.
- The new bag keeps a reference to the discarded one for traceability.

This makes the flow forward-only and explicit.

Why this is a compensating, forward-only flow:

In real systems, trying to revert the past usually creates more problems than it solves:
- It breaks traceability.
- It hides operational costs.
- It creates inconsistent or hard-to-explain states.

This model takes a different approach:
- The past is not rewritten.
- The failure is acknowledged by moving the bag to DISCARDED.
- The system compensates by creating a new bag and continuing the flow.

This keeps the system:
- Honest about what happened.
- Auditable.
- And resilient to common operational failures.

Trade-offs:

Costs:
- More bags can be consumed.
- Additional metrics and controls may be needed to avoid abuse.

Benefits:
- The picking flow is not blocked by local failures.
- Correct operational decisions are not penalized.
- Traceability is preserved.
- The system remains forward-only and explainable.
- Operational failures become first-class states instead of hidden exceptions.

Out of Scope:

This mini-project does not address:
- Bag capacity or physical constraints.
- Picking area limits.
- Orchestration or batch logic.
- UI or operator workflows.

Its only goal is to model bag lifecycle and recycling as explicit domain states and transitions.

Where this lives in the code:

This behavior is modeled mainly in:
- BagStatus.
- Bag.
