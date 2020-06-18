
    
    const headers = {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    };
  
    const BASE_URL = '/api';
  
    async function loginUser(loginValue,passwordValue) { //al api.js
      const res = {
        "user": {
          "name": "Nacho Scocco",
          "email": "qwe@gmail.com",
          "password": "password311",
          "_id": "5ede564a853cd27b821cee5c",
          "creationDate": null,
          "country": "AR",
          "isAdmin": false
        },
        "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRhY3MiLCJpZCI6IjVlZGU1NjRhODUzY2QyN2I4MjFjZWU1YyIsImV4cCI6MTU5MTY2OTQ4Nn0.kSAbfZVG0p9ueU91zrye1CZRFf_B3JBI2lLIMgnA7cc"
      }
      try{
        return new Promise(resolve => {
          setTimeout(() => {
            resolve(res);
          }, 2000);
        })
      }catch(error){
        console.log(error)
      }
      /*return await fetch(`${BASE_URL}/`, {
            method:'POST',
            headers: headers,
            body: JSON.stringify(item),
          });
        }*/
    }
  
    async function createUser(nameValue,loginValue,passwordValue) { //esto va al api.js
      try{
        const res = {
          "user": {
            "name": "Nacho Scocco",
            "email": "qwe@gmail.com",
            "password": "password311",
            "_id": "5ede564a853cd27b821cee5c",
            "creationDate": null,
            "country": "AR",
            "isAdmin": false
          },
          "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRhY3MiLCJpZCI6IjVlZGU1NjRhODUzY2QyN2I4MjFjZWU1YyIsImV4cCI6MTU5MTY2OTQ4Nn0.kSAbfZVG0p9ueU91zrye1CZRFf_B3JBI2lLIMgnA7cc"
        }
        return new Promise(resolve => {
          setTimeout(() => {
            resolve(res);
          }, 2000);
        })
      }catch(error){
        console.log(error)
      }
      /*return await fetch(`${BASE_URL}/`, {
            method:'POST',
            headers: headers,
            body: JSON.stringify(item),
          });
        }*/
    }
  export { loginUser, createUser };