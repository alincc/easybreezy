**PROFILE**

HR component,  Profile Entity

**Update profile information**
POST 
`api/hr/profile/personal-data`

BODY 
```
{
	"birthday": "1966-10-14",
	"gender": "FEMALE",
	"about": "Some info about me Test",
	"firstName": "test Test",
	"lastName": "test Test",
	"workStack": "Kotlin, php"
}
```

**Add messenger to profile**
POST 
`api/hr/profile/add-messengers`

BODY
```
{
	"messengers" : [
		{
			"type": "skype",
			"username": "qwebot"
		},
		{
			"type": "telegram",
			"username": "qwert"
		}
	]
}
```

**Update profile contact details**
POST 
`api/hr/profile/contact-details`

BODY
```
{
	"phones": ["123", "456"],
	"emails": ["qwert@qwert.my", "test@test.my"]
}
```


**USERS**

**Authorization**
POST 
`/api/login`
BODY
```
{
	"email" : "qwert@qwert.my", 
	"password": "123"
}
```

**Invite user**
POST 
`/api/users/invite`
BODY 
```
{
	"email": "qwert@qwert.my", 
	"role": "MEMBER"
}
```
**Confirm user**
POST 
`/api/users/confirm`
BODY 
```
{
	"token": "Z7YyUhDNZxSDvYZRwHDJVBBPeGMbvCJfnQWCgY6f", 
	"password": "123", 
	"firstName": "asd", 
	"lastName" : "asd"
}
```
