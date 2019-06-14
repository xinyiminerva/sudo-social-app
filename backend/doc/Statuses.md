# Statuses
Here are defined any HTTP Status Codes which may be returned by the API methods.  
[[Back to main page]](https://bitbucket.org/mae5/cse216_aztecs/src/backend/backend/README.md)

| Status Code | Description |
| :--- | :--- |
| 200 | Everything is ok. |
| 400 | Some part of the request was written incorrectly. |
| **401** | The requested resource requires that the "Authorization" header contain the user's authorization token. |
| **403** | The user is not authorized to access the requested resource. |
| **404** | The resource (user, like, or message) that was requested does not exist in the database. |
| **409** | The resource cannot be created because it would overwrite another existing resource. |
| 500 | Something went wrong when the server tried to query the database. |
| 501 | The requested HTTP method (GET, POST, ...) cannot be called on the specified API endpoint. |
