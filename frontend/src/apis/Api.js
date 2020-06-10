import { getLocation } from "./GeolocationApi"

class Api {

    constructor() {
      this.authToken = localStorage.getItem("id_token");
    }
  
    headers = {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    };
  
    BASE_URL = '/api';
  
    createHeaders() {
      return this.authToken ? {
        ...this.headers,
        'Authorization': 'Bearer ' + this.authToken
      } : this.headers;
    }

    async getAll() {
      return await fetch(this.BASE_URL, {
        method: 'GET',
        headers: this.createHeaders()
      });
    }
  
    async getById(id) {
      return await fetch(`${this.BASE_URL}/${id}`, {
        method: 'GET',
        headers: this.createHeaders()
      });
    }
  
    async delete(id) {
      return await fetch(`${this.BASE_URL}/${id}`, {
        method: 'DELETE',
        headers: this.createHeaders()
      });
    }
  
    async update(item) {
      return await fetch(`${this.BASE_URL}/${item.id}`, {
        method:'PUT',
        headers: this.createHeaders(),
        body: JSON.stringify(item),
      });
    }
  
    async create(item) {
      return await fetch(this.BASE_URL, {
        method:'POST',
        headers: this.createHeaders(),
        body: JSON.stringify(item),
      });
    }

    /*async findUser(loginValue,passwordValue) { //al api.js
      try{
        return new Promise(resolve => {
          setTimeout(() => {
            resolve({token : 'asdasdas', sessionId: 'asdasd', nameValue: 'Nacho Schocco'});
          }, 2000);
        })
      }catch(error){
        console.log(error)
      }
      return await fetch(`${this.BASE_URL}, {
            method:'POST',
            headers: this.createHeaders(),
            body: JSON.stringify(item),
          });
        
    }}*/

  async getCountryDataByIsoDate(loginValue,passwordValue) {
    try{
      const data = { 
        "timeseries":
        [{
        "number": 136,
        "confirmed": 230,
        "deaths": 20,
        "recovered": 246414,
        "date": "5/14/20"
        },{
        "number": 137,
        "confirmed": 9707,
        "deaths": 45,
        "recovered": 250,
        "date": "4/3/20"
        }],
        "totals": { "confirmed": 141777,
                    "deaths": 8589,
                    "recovered": 24641,} 
        }
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
      const data = [
        {
          "_id": "5ede4f65853cd27b821cecec",
          "countryregion": "Argentina",
          "lastupdate": "2020-06-08T14:42:00.008Z",
          "location": {
            "lat": -38.4161,
            "lng": -63.6167
          },
          "countrycode": {
            "iso2": "AR",
            "iso3": "ARG"
          },
          "confirmed": 22794,
          "deaths": 664,
          "recovered": 6909,
          "timeseries": []
        },
        {
          "_id": "5ede4f65853cd27b821cecf9",
          "countryregion": "Bolivia",
          "lastupdate": "2020-06-08T14:42:00.008Z",
          "location": {
            "lat": -16.2902,
            "lng": -63.5887
          },
          "countrycode": {
            "iso2": "BO",
            "iso3": "BOL"
          },
          "confirmed": 13643,
          "deaths": 465,
          "recovered": 2086,
          "timeseries": []
        },
        {
          "_id": "5ede4f65853cd27b821cecfb",
          "countryregion": "Brazil",
          "lastupdate": "2020-06-08T14:42:00.008Z",
          "location": {
            "lat": -14.235,
            "lng": -51.9253
          },
          "countrycode": {
            "iso2": "BR",
            "iso3": "BRA"
          },
          "confirmed": 691758,
          "deaths": 36455,
          "recovered": 283952,
          "timeseries": []
        },
        {
          "_id": "5ede4f65853cd27b821ced05",
          "countryregion": "Chile",
          "lastupdate": "2020-06-08T14:42:00.008Z",
          "location": {
            "lat": -35.6751,
            "lng": -71.543
          },
          "countrycode": {
            "iso2": "CL",
            "iso3": "CHL"
          },
          "confirmed": 134150,
          "deaths": 1637,
          "recovered": 108150,
          "timeseries": []
        },
        {
          "_id": "5ede4f65853cd27b821ced5a",
          "countryregion": "Paraguay",
          "lastupdate": "2020-06-08T14:42:00.008Z",
          "location": {
            "lat": -23.4425,
            "lng": -58.4438
          },
          "countrycode": {
            "iso2": "PY",
            "iso3": "PRY"
          },
          "confirmed": 1135,
          "deaths": 11,
          "recovered": 575,
          "timeseries": []
        },
        {
          "_id": "5ede4f65853cd27b821ced5b",
          "countryregion": "Peru",
          "lastupdate": "2020-06-08T14:42:00.008Z",
          "location": {
            "lat": -9.19,
            "lng": -75.0152
          },
          "countrycode": {
            "iso2": "PE",
            "iso3": "PER"
          },
          "confirmed": 196515,
          "deaths": 5465,
          "recovered": 86219,
          "timeseries": []
        },
        {
          "_id": "5ede4f65853cd27b821ced80",
          "countryregion": "Uruguay",
          "lastupdate": "2020-06-08T14:42:00.008Z",
          "location": {
            "lat": -32.5228,
            "lng": -55.7658
          },
          "countrycode": {
            "iso2": "UY",
            "iso3": "URY"
          },
          "confirmed": 845,
          "deaths": 23,
          "recovered": 730,
          "timeseries": []
        }
      ]

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
}
  export default Api;
  