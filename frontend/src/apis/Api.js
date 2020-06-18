import mock from "./mock"

class Api {

    constructor() {
      this.authToken = localStorage.getItem("id_token");
      this.userSessionId = localStorage.getItem("id_session");
      this.countryMap = localStorage.getItem("countriesList");
    }
  
    headers = {
      'Accept': 'application/json',
    };
  
    BASE_URL = 'https://32ddbafd6091.ngrok.io';

    createHeaders() {
      return !!this.authToken ? {
        ...this.headers,
        'Authorization': 'Bearer ' + this.authToken
      } : this.headers;
    }
  
    async getCountryList(isoList){
      try{
        const data = mock.countriesNameISo 
  
        return new Promise(resolve => {
          setTimeout(() => {
            resolve(data);
          }, 500);
        })
      }catch(error){
        console.log(error)
      }
      /*
      return await fetch(`${this.BASE_URL}/api/countries/names`, {
        method:'GET',
        headers: this.createHeaders(),
      });*/
    }

    async createCountryList(nombreLista,listarray){
      try{
  
        return new Promise(resolve => {
          setTimeout(() => {
            resolve();
          }, 500);
        })
      }catch(error){
        console.log(error)
      }
      /*return fetch( `${this.BASE_URL}/api/user/${this.userSessionId}/lists`,{
        method:"post",
        headers: this.createHeaders(),
        body:JSON.stringify({
            "name": nombreLista,
            "countries":listarray
        })
      })*/
    }

    async deleteCountryList(nombreLista,listarray){
      try{
  
        return new Promise(resolve => {
          setTimeout(() => {
            resolve();
          }, 500);
        })
      }catch(error){
        console.log(error)
      }
      /*return fetch( `${this.BASE_URL}/api/user/${this.userSessionId}/lists/${listId}`,{
        method:"DELETE",
        headers: this.createHeaders(),
      })*/
    }

    async editCountryList(name,listId,countries){
      try{
  
        return new Promise(resolve => {
          setTimeout(() => {
            resolve();
          }, 500);
        })
      }catch(error){
        console.log(error)
      }

      /*return fetch( `${this.BASE_URL}/api/user/${this.userSessionId}/lists/${listId}`,{
        method:"PUT",
        headers: this.createHeaders(),
        body:JSON.stringify({
            "name": name,
            "countries":countries
        })
      })*/
    }

  async getCountriesDataByDate(isoList,dateinicial,datefinal) {
    try{
      const data = mock.singleCountryLastday

      return new Promise(resolve => {
        setTimeout(() => {
          resolve(data);
        }, 500);
      })
    }catch(error){
      console.log(error)
    }
    /*const res = await await fetch(`${this.BASE_URL}/api/countries/timeseries?`, {
          method:'POST',
          headers: this.createHeaders(),
          body: JSON.stringify(item),
        });

      if(res.ok)
        const data = await res.json()
      
      return data.timeseries
      }*/
  }

  async getCountriesDataByDays(iso,startDay,endDay) {
    try{
      const data = mock.nearWithOffset 

      return new Promise(resolve => {
        setTimeout(() => {
          resolve(data);
        }, 500);
      })
    }catch(error){
      console.log(error)
    }
    /*const res = await await fetch(this.BASE_URL, {
          method:'POST',
          headers: this.createHeaders(),
          body: JSON.stringify(item),
        });

      if(res.ok)
        const data = await res.json()
      
      return data.timeseries
      }*/
  }

  async getCountriesData(isoList) {
    try{
      const data = mock.nearordered 

      return new Promise(resolve => {
        setTimeout(() => {
          resolve(data);
        }, 500);
      })
    }catch(error){
      console.log(error)
    }
    /*const res = await await fetch(this.BASE_URL, {
          method:'POST',
          headers: this.createHeaders(),
          body: JSON.stringify(item),
        });

      if(res.ok)
        const data = await res.json()
      
      return data.timeseries
      }*/
  }
  
  async getNearCountries() {
    try{
      const data = mock.nearordered

      return new Promise(resolve => {
        setTimeout(() => {
          resolve(data);
        }, 500);
      })

      /*let position = await getLocation()
      const lat = position.coords.latitude;
      const lng = position.coords.longitude;

      return await fetch(`${this.BASE_URL}/api/countries?lat=${lat}&lon=${lng}}`, {
        method: 'GET',
        headers: this.createHeaders()
      });*/

    }catch(e){
      console.log(e)
    }
  }

  async getUserLists() {
    try{
      const data = mock.userLists

      return new Promise(resolve => {
        setTimeout(() => {
          resolve(data);
        }, 500);
      })

      /*
      return await fetch(`${this.BASE_URL}/api/user/${this.userSessionId}/lists`, {
        method: 'GET',
        headers: this.createHeaders()
      });*/

    }catch(e){
      console.log(e)
    }
  }
  
  async deleteUserList(listId) {
    try{

      return new Promise(resolve => {
        setTimeout(() => {
          resolve();
        }, 500);
      })

      /*return await fetch(`${this.BASE_URL}/api/${this.userSessionId}/lists/${listId}`,{
            method:"DELETE",
            headers: this.createHeaders()
            })
      ;*/

    }catch(e){
      console.log(e)
    }
  }

  loginUser(loginValue,passwordValue) { //al api.js
    const res = mock.loginUser
    
    try{
      return new Promise(resolve => {
        setTimeout(() => {
          resolve(res);
        }, 2000);
      })
    }catch(error){
      console.log(error)
    }
    /*return await fetch(`${BASE_URL}/api/login`, {
          method:'POST',
          headers: this.createHeaders(),
          body: JSON.stringify(
            {
              email: loginValue,
              password: passwordValue
            }
          ),
        });
      }*/
  }

  loginUserWithGoogle(tokenId,mail,name,country) { //al api.js
    const res = mock.loginUser
    
    try{
      return new Promise(resolve => {
        setTimeout(() => {
          resolve(res);
        }, 2000);
      })
    }catch(error){
      console.log(error)
    }
    /*return await fetch(`${BASE_URL}/auth/google`, {
          method:'POST',
          headers: this.createHeaders(),
          body: JSON.stringify(
            {
              token: tokenId,
              email: mail,
              name: name
            }
          ),
        });
      }*/
  }

  createUser(nameValue,loginValue,passwordValue,countryIso) { //esto va al api.js
    try{
      const res = mock.signUp
      
      return new Promise(resolve => {
        setTimeout(() => {
          resolve(res);
        }, 2000);
      })
    }catch(error){
      console.log(error)
    }
    /*return await fetch(`${BASE_URL}/api/signup`, {
          method:'POST',
          headers: this.createHeaders(),
          body: JSON.stringify({
                        name: nameValue,
                        email: loginValue,
                        password: passwordValue,
                        country: countryIso,
                        }),
                      });
      */
  }
}
  export default Api;
  