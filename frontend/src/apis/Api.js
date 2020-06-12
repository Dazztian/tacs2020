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

    countrNameConvertor(countriesNamesArray){

    }

  
    async getCountryList(){
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

  async getCountryDataByDate(isoList,dateinicial,datefinal) {
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

  async getCountryDataByDays(iso,startDay,endDay) {
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
  
  async getNearCountries() {
    try{
      const data = mock.near

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
          headers: headers,
          body: JSON.stringify(item),
        });
      }*/
  }

  createUser(nameValue,loginValue,passwordValue) { //esto va al api.js
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
          headers: headers,
          body: JSON.stringify(item),
        });
      }*/
  }
}
  export default Api;
  