# Like Objects
Here are defined all JSON objects which are used to represent likes on messages within the system.  
[[Back to main page]](https://bitbucket.org/mae5/cse216_aztecs/src/backend/backend/README.md)

## Like Object

| Field | Type | Description |
| :--- | :--- | :--- |
| liked | boolean | True if the author liked the post, false if they disliked it. |
| author_id | integer | The ID number of the user who posted the like. |
| message_id | integer | The ID number of the message with which the like is associated. |

## POST Like Object

| Field | Type | Description |
| :--- | :--- | :--- |
| liked | boolean | True if the author liked the post, false if they disliked it. |
| message_id | integer | The ID number of the message with which the like is associated. |

## PUT Like Object

| Field | Type | Description |
| :--- | :--- | :--- |
| liked | boolean | True if the author liked the post, false if they disliked it. |
