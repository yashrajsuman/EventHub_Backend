package com.eventhub.event;

class EventNotFoundException extends RuntimeException { }
class EventFullException extends RuntimeException { }
class DuplicateRegistrationException extends RuntimeException { }
class EventCapacityException extends RuntimeException { }
