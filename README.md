# demo
demo
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CreateConsentRequest",
  "type": "object",
  "required": ["data"],
  "properties": {
    "data": {
      "type": "object",
      "required": ["loggedUser", "permissions", "expirationDateTime", "transactionFromDateTime", "transactionToDateTime", "businessEntity"],
      "properties": {
        "loggedUser": {
          "type": "object",
          "required": ["document"],
          "properties": {
            "document": {
              "type": "object",
              "required": ["identification", "rel", "type"],
              "properties": {
                "identification": {
                  "type": "string",
                  "pattern": "^[0-9]{11}|[0-9]{14}$"
                },
                "rel": {
                  "type": "string",
                  "enum": ["CONSUMER"]
                },
                "type": {
                  "type": "string",
                  "enum": ["CPF", "CNPJ"]
                }
              }
            }
          }
        },
        "permissions": {
          "type": "array",
          "items": {
            "type": "string"
          },
          "minItems": 1
        },
        "expirationDateTime": {
          "type": "string",
          "format": "date-time"
        },
        "transactionFromDateTime": {
          "type": "string",
          "format": "date-time"
        },
        "transactionToDateTime": {
          "type": "string",
          "format": "date-time"
        },
        "businessEntity": {
          "type": "object",
          "required": ["document"],
          "properties": {
            "document": {
              "type": "object",
              "required": ["identification", "type"],
              "properties": {
                "identification": {
                  "type": "string",
                  "pattern": "^[0-9]{14}$"
                },
                "type": {
                  "type": "string",
                  "enum": ["CNPJ"]
                }
              }
            }
          }
        }
      }
    }
  }
}
