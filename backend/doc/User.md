# User Objects
Here are defined all JSON objects which are used to represent individual users within the system.  
[[Back to main page]](https://bitbucket.org/mae5/cse216_aztecs/src/backend/backend/README.md)

## User Object

| Field | Type | Description |
| :--- | :--- | :--- |
| id | integer | The ID number of the user. |
| name | string | The user's username. |
| pwd_hash | string | The user's salted+hashed password. |
| salt | string | The user's unique salt value. |
| bio | string | The user's bio. Defaults to "Smash that mf like button" |

~~## POST User Object~~

~~| Field | Type | Description |~~
~~| :--- | :--- | :--- |~~
~~| name | string | The user's username. |~~

## PUT User Object

| Field | Type | Description |
| :--- | :--- | :--- |
| pwd_hash | string | The user's new salted+hashed password. |
| pwd_hash | string | The user's new bio, if any. |

## Authorized User Object

| Field | Type | Description |
| :--- | :--- | :--- |
| id | integer | The ID number of the user. |
| name | string | The user's username. |
| pwd_hash | string | The user's salted+hashed password. |
| salt | string | The user's unique salt value. |
| token | string | A JWT authentication token. This string should be included in requests in the "Authorization" header, as a bearer token for any API endpoint marked with "**Authorization Token Required**". |
