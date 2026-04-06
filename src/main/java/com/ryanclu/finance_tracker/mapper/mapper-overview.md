Mappers do one simple job: convert between entities and DTOs.

They sit between the service layer and the repository layer. When data comes in from a request, the mapper converts the
request DTO into an entity so it can be saved. When data goes out to a response, the mapper converts the entity into a
response DTO so the controller can return it.