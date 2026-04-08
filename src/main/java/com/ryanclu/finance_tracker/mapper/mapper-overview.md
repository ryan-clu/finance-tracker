Mapper Overview
---

Mappers do one simple job: convert between entities and DTOs.

Mappers are a "tool" that the Service layer uses. It is not an independent "middleware layer".

They sit between the service layer and the repository layer. When data comes in from a request, the mapper converts the
request DTO into an entity so it can be saved. When data goes out to a response, the mapper converts the entity into a
response DTO so the controller can return it.

//////////////////////////////



Request flow (client → database):
---

1. HTTP request hits the controller — the controller receives the raw JSON body and Spring automatically deserializes it
   into the request DTO (like CreateTransactionRequest). The controller doesn't do any logic — it just passes the DTO to
   the service layer.
2. The service layer receives the request DTO, applies business logic — looks up the authenticated user, validates that
   referenced entities exist and belong to that user (e.g., "does account 5 actually belong to this user?"), enforces
   any business rules. Then it calls the mapper to convert the request DTO into an entity.
3. The mapper takes the request DTO plus any entities the service looked up (User, Account, Category) and builds the
   entity object.
4. The service passes the entity to the repository, which persists it to the database.

Spring/Jackson (auto-converts JSON to Req DTO) ->
Controller (receives Req DTO, passes to Service) ->
Service (receives Req DTO, looks up User/Account/Category entities, applies business logic, calls Mapper) ->
Mapper (receives Req DTO + looked-up entities, builds and returns Entity) ->
Service (receives Entity, passes to Repository)->
Repository (persists Entity to database)

///

Response flow (database → client):
---

1. The repository queries the database and returns an entity (or list of entities) to the service layer.
2. The service layer receives the entity, does any additional computation (like calculating spentAmount for budgets),
   then calls the mapper to convert the entity into a response DTO.
3. The mapper takes the entity, flattens relationships, excludes sensitive fields, adds any derived fields (like
   isDefault), and returns the response DTO.
4. The service returns the response DTO to the controller.
5. The controller returns the response DTO, and Spring automatically serializes it into JSON for the HTTP response.

Repository (queries database, returns Entity) ->
Service (receives Entity, does any computations, calls Mapper) ->
Mapper (receives Entity, flattens/transforms, returns Res DTO) ->
Service (receives Res DTO, returns to Controller) ->
Controller (receives Res DTO, Spring/Jackson auto-converts to JSON for client)

//////////////////////////////



Where the "Mapper" sits.
---

The mapper doesn't sit independently between the service and repository like a middleware layer that data passes
through.

The service is the orchestrator — it calls the repository directly and it calls the mapper directly. The mapper is a
tool the service uses, not a layer data flows through automatically. The service says "hey mapper, convert this for me"
and
gets back the result.

//////////////////////////////



TRANSACTION DATA FLOW AS AN EXAMPLE
---

Creating a transaction (request):
---

Client sends JSON:
{
"amount": 45.99,
"description": "Lunch at Chipotle",
"transactionType": "EXPENSE",
"transactionDate": "2025-04-08",
"accountId": 3,
"categoryId": 1
}

-> Spring/Jackson auto-converts to CreateTransactionRequest record

-> Controller receives CreateTransactionRequest, passes to TransactionService

-> TransactionService:

1. Gets authenticated User from security context
2. Calls accountRepository.findByIdAndUserId(3, userId)
   -> verifies account 3 belongs to this user
3. Calls categoryRepository.findById(1)
   -> verifies category exists
4. Calls transactionMapper.toEntity(request, user, account, category)

-> TransactionMapper.toEntity() builds Transaction entity using Builder:

- amount = 45.99
- description = "Lunch at Chipotle"
- transactionType = EXPENSE
- transactionDate = 2025-04-08
- user = User entity from step 1
- account = Account entity from step 2
- category = Category entity from step 3

-> TransactionService calls transactionRepository.save(transaction)

-> Repository persists to database


Retrieving a transaction (response):
---

Client sends: GET /api/v1/transactions/7

-> Controller receives request, passes id=7 to TransactionService

-> TransactionService:

1. Gets authenticated User from security context
2. Calls transactionRepository.findByIdAndUserId(7, userId)
   -> returns Transaction entity with nested Account and Category

-> TransactionService calls transactionMapper.toResponse(transaction)

-> TransactionMapper.toResponse() builds TransactionResponse:

- id = transaction.getId()                    -> 7
- amount = transaction.getAmount()            -> 45.99
- description = transaction.getDescription()  -> "Lunch at Chipotle"
- transactionType = EXPENSE
- transactionDate = 2025-04-08
- accountId = transaction.getAccount().getId()      -> 3
- accountName = transaction.getAccount().getName()  -> "Chase Credit Card"
- categoryId = transaction.getCategory().getId()    -> 1
- categoryName = transaction.getCategory().getName() -> "Food & Dining"
- createdAt = transaction.getCreatedAt()

-> Service returns TransactionResponse to Controller

-> Spring/Jackson converts to JSON:
{
"id": 7,
"amount": 45.99,
"description": "Lunch at Chipotle",
"transactionType": "EXPENSE",
"transactionDate": "2025-04-08",
"accountId": 3,
"accountName": "Chase Credit Card",
"categoryId": 1,
"categoryName": "Food & Dining",
"createdAt": "2025-04-08T12:30:00"
}

-> Client receives JSON

//////////////////////////////



JACKSON
---

Jackson comes with Spring Boot — yes. When we added spring-boot-starter-web to our pom.xml, Jackson was pulled in as a
transitive dependency. Spring Boot auto-configures it with sensible defaults, so it's ready to go without any setup on
our part.

Spring handles it for us — yes, almost entirely. When a request comes in with a JSON body and your controller method has
a parameter annotated with @RequestBody (which we'll add in Phase 5), Spring tells Jackson "deserialize this JSON string
into this Java object." Jackson looks at the JSON property names, matches them to the Record's field names (or a class's
setter methods), and constructs the object. Going the other direction, when your controller method returns an object,
Spring tells Jackson "serialize this Java object into JSON." Jackson reads the field names (or getter methods) and
builds the JSON string.

The one thing worth knowing is that this matching is name-based. If your JSON has "accountType": "CHECKING" and your
Record has a field called accountType, Jackson connects them. If the names don't match, the mapping fails. This is why
the field names on your DTOs matter — they define your API's JSON contract.

What Jackson actually is — it's a general-purpose Java library for converting between Java objects and JSON. It's not
Spring-specific at all — any Java application can use it. Spring just integrates it as the default
serialization/deserialization engine for HTTP request and response bodies. Think of it as the translator sitting at the
very edges of your application: JSON comes in from the outside world, Jackson turns it into Java objects your code can
work with, and when your code is done, Jackson turns the Java objects back into JSON for the outside world.

You'll almost never interact with Jackson directly in this project. The only time you might is if you need to customize
how a specific field is serialized — for example, formatting dates a certain way — which you'd do with a Jackson
annotation on the DTO field. But that's a Phase 7 concern at most.