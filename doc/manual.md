## Create mongo user for db:

```
use HOBBY

db.createUser({
  user: "dbuser",
  pwd: "secret",
  roles:[
    {role: "readWrite", db: "HOBBY" },
    {role: "readWrite", db: "SEASON" },
    {role: "readWrite", db: "PROGRAMMING" }
  ]
})
```