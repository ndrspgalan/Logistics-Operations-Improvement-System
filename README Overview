# LOGISTICS OPERATIONS IMPROVEMENT SYSTEM

## Problem Context

Large logistics systems rarely fail because of algorithms.
They fail because the software model does not represent physical and operational constraints accurately.

This repository explores how small domain changes can prevent invalid operational states from entering the system.

## System Overview

```mermaid
flowchart LR

Orders[Customer Orders]

Planning["Planning Domain<br/>Picking Windows"]

Packing["Packing Domain<br/>Capacity Policy"]

Picking[Picking Execution]

Returns["Reverse Logistics<br/>Unpack Flow"]

Inventory["Inventory Domain<br/>Bin Stock"]

Orders --> Planning
Planning --> Picking
Picking --> Packing
Packing --> Picking

Picking --> Returns
Returns --> Inventory

Inventory --> Planning
```

### Domain Interaction Overview

The system models operational constraints across four main domains.

**Planning Domain**
- Picking windows.
- Time-aware urgency states.
- Capacity-aware order assignment.

**Packing Domain**
- Effective bag capacity rules.
- Fragile item handling.
- Ice compensation for cold chains.

**Inventory Domain**
- SKU-to-bin mapping.
- Expiration date integrity.
- Prevention of mixed-date inventory placement.

**Reverse Logistics Domain**
- Returned bag manifest.
- Explicit product disposition decisions.
- Reintroduction eligibility rules.

Each domain owns its rules and protects its invariants.
The system avoids centralized "god logic" and instead keeps operational constraints
close to the domain where they belong.

## Introduction

This repository contains a set of design improvements applied to a logistics operations system.
It is not a framework showcase nor an algorithmic demo.
Its goal is to model real and recurring operational problems and to propose technical solutions
with explicit and conscious trade-offs.
The core premise is simple:

## System Philosophy

In real systems, the problem is rarely "the code".
The problem is usually and invalid system state caused by physical, temporal or operational constraints
that the original model did not represent explicitly.

Each mini-project introduces a local change to the domain model in order to make
the system more honest, predictable, and explainable.

## Domain invariants

The system enforces several invariants at construction time and during state transitions.
These invariants prevent invalid operational states from entering the domain model.

Examples include:

- A "PickingWindow" must always have a strictly positive capacity.
- A "Bag" cannot transition from "CLOSED" to "DISCARDED".
- Products can only be reintroduced into inventory if their expiration date respects the defined eligibility threshold.
- Expiration matching never suggests bins with different expiration dates.

By enforcing these invariants directly in the domain model,
the system avoids propagating inconsistent states to downstream processes.

Invalid operational states are prevented early instead of being corrected later.

## Idempotent Operations

Operational systems frequently experience retries due to network failures,
worker restarts or message redelivery.

For this reason, several critical domain operations are designed to be idempotent.
Executing them multiple times produces the same final state.

Examples include:
- Discarding a bag and creating a replacement.
- Cancelling a confirmed picking window.
- Confirming and executing an unpack disposition plan.

If these operations are executed more than once, the system avoids producing duplicated side effects
or inconsistent state transitions.

This design protects the domain model from retry scenarios that are common ind distributed system.

## Operational Hot Paths

When the system operates at larger scale, several execution paths become critical for performance.

The current implementation favors clarity and deterministic rules. However, in real production systems the following areas
would likely require optimization:

### Window Selection

The selection of the next picking window becomes a frequent operation in large installations.
Potential optimizations include:
- Pre-sorted planning queues.
- Priority indexing by deadline and urgency.
- Cached capacity availability.

### Expiration Matching

Expiration matching currently performs a linear scan over available bins.

At larger scale, this would likely be replaced by:
- Indexing by "SKU + expiration date".
- Pre-grouped inventory maps.
- Inventory services providing indexed lookups.

### Capacity Evaluation

Capacity checks are deterministic but may become frequent during planning bursts.
Possible optimizations include:
- Caching bag capacity evaluations.
- Precomputing product packing characteristics.
- Separating packing simulation from planning logic.

## Concurrency Model

This repository models domain rules rather than infrastructure concerns.
Concurrency mechanisms such as:
- Distributed locking.
- Optimistic concurrency control.
- Idempotent message processing.
- Retry strategies.
- Transactional boundaries.

Are intentionally outside the scope of this repository.

The goal of the project is to make operational constraints explicit at the domain level.

In real production systems these domain rules would be executed within infrastructure layers
responsible for concurrency control and consistency guarantees.

## Trade-Off Metrics

Operational improvements rarely come without cost.

Several changes introduced in this model intentionally prioritize safety, traceability and predictability
over maximum local efficiency.

To make these trade-offs explicit, the "ScenarioRunner" collects simple operational metrics during simulation:
- **Capacity rejection rate**
    Measures how often loads are rejected due to conservative capacity rules.
- **Compensation rate**
    Measures how often bag recycling flows are triggered due to operational failures.
- **Manual intervention rate**
    Measures how often operators must make explicit decisions during return recovery.

These metrics are scenario-driven and are not intended to represent real operational statistics.
Their purpose is to illustrate the systemic cost of design decisions.

## Scenario-Driven Validation

The "ScenarioRunner" act as a lightweight operational simulation tool.

Rather than serving as a demo layer, it validates important domain behaviors through structured scenarios.
Each scenario simulates common operational situations such as:
- Late order cancellation.
- Capacity overflow.
- Expiration date matching.
- Planning behavior under increased scale.

During execution the runner verifies that key domain invariants remain true.

If an invariant is violated, the scenario fails immediately,
making structural weaknesses visible during development.

This approach ensures that the domain model behaves correctly under realistic operational conditions.

## Operational Baselines

The metrics produced by the "ScenarioRunner" illustrate system behavior,
but they require reference points to be interpreted.

The following baseline values represent typical operational ranges observed in
high-volume grocery logistics environments.

|Metric|Typical Range|Operational Meaning|
|------|------|------|
Capacity rejection rate|5-20%|Conservative packing rules prevent unsafe bags but reduce packing density|
Bag compensation rate|1-5%|Bags occasionally fail or must be replaced during picking|
Manual intervention rate|30-60%|Return processing frequently requires explicit operator decisions|

These values are not strict but realistic reference ranges used to interpret simulation results.

## Economic Impact of Trade-Offs

Operational rules often introduce measurable economic costs.
However, these costs are typically lower than the cost of systemic operational failures.

The following simplified estimates illustrate the economic dimension of the modeled trade-offs.

## Capacity Rejection

Conservative packing rules may increase the number of bags required. Typical assumptions:
- Average bag cost: €0.15-€0.30.
- Average order: 4-6 bags.
- Rejection rate: 5-20%.

Example: if a warehouse processes 10.000 orders/day and conservatory packing adds 0.3 additional bags per order:
3.000 extra bags/day * €0.20 ≈ €600/day ≈ €219.000/year.

However, this cost prevents:
- Bag breakage.
- Product damage.
- Repacking Labor.
- Delivery failures.

---

### Bag Compensation

Operational failures occasionally require replacing a bag. Typical assumptions:
- Compensation rate: 1-5%.
- Average bag cost: €0.20.

Example: 10.000 orders/day * 3% compensation rate = 300 replacement bags.
300 * €0.20 ≈ €60/day ≈ €21.900/year.

This cost preserves picking flow and avoids worker delays.

 ---

 ### Manual Intervention in Returns

 Explicit operator decisions increase processing time but significantly reduce inventory corruption.
 Typical assumptions:
 - Additional decisions time: 3-6 seconds per line.
 - Average returned order: 4 lines.
 - Operator cost: €15/hour.

 Example:
5 extra seconds * 4 lines = 20 seconds/order.
1.000 returns/day --> ~5.5 hours additional labor.
5.5 * €15 ≈ €82/day ≈ €30.000/year.

This cost avoids:
- Expiration date manipulation.
- Inventory desynchronization.
- Expensive cycle counts.
- Audit corrections.
