# Message Objects
Here are defined all JSON objects which are used to represent messages within the system.  
[[Back to main page]](https://bitbucket.org/mae5/cse216_aztecs/src/backend/backend/README.md)

## Message Object

| Field | Type | Description |
| :--- | :--- | :--- |
| id | integer | The ID number of the message. |
| author_id | integer | The ID number of the user who posted the message. |
| parent_id | (integer OR undefined) | The ID number of the message which this message is commenting on. Undefined if the comment has no parent. |
| message | string | The actual text of the message. |
| likes | integer | The net number of likes and dislikes on the message |
| liked | (boolean OR undefined) | True if the user who made the request liked the message, false if they disliked it, undefined otherwise. |
| comments | integer | The number of messages which directly refer to this comment as their parent. |

## POST Message Object

| Field | Type | Description |
| :--- | :--- | :--- |
| message | string | The actual text of the message. |
| parent_id | (integer OR undefined) | The ID number of the message which this message is commenting on. Undefined if the comment has no parent. |

## PUT Message Object

| Field | Type | Description |
| :--- | :--- | :--- |
| message | string | The actual text of the message. |
