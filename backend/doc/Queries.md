# Queries
The following are the definitions of specific HTTP query strings which may be applied to certain API endpoints to filter the results.  
[[Back to main page]](https://bitbucket.org/mae5/cse216_aztecs/src/backend/backend/README.md)


| Endpoint+Query | Type | Methods | Description |
| :--- | :--- | :--- | :--- | :--- |
/api/users?username=(name) | string | GET | This returns the corresponding [Authorized User Object](https://bitbucket.org/mae5/cse216_aztecs/src/backend/backend/doc/User.md) given the username. Throws 404 Not Found if a corresponding entry cannot be found in the database. | 
/api/users?authenticate=(id) | string (will be converted to Integer), string | GET | Throws AuthorizationFailedException("Login Failed: please enter password") |
| /api/users?authenticate=(id)&hash=(pwd_hash) | string | GET | This validates the given username and hash returns the corresponding [Authorized User Object](https://bitbucket.org/mae5/cse216_aztecs/src/backend/backend/doc/User.md). |
| /api/messages?thread=(id) | integer | GET | Instead of returning all conversation threads, this query will cause the request to return an array of [Message Objects](https://bitbucket.org/mae5/cse216_aztecs/src/backend/backend/doc/Message.md) representing all comments on the message with the provided ID number. |
| /api/likes?user=(id) | integet | GET | Instead of returning all likes in the system, this query will filter the array down to just the likes authored by the user with the provided ID number. |
