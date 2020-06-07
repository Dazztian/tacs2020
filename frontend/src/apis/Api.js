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

    async getNearCountries() {
      try{
        let position = await getLocation()
        const lat = position.coords.latitude;
        const lng = position.coords.longitude;

        return await fetch(`${this.BASE_URL}/${id}`, {
          method: 'GET',
          headers: this.createHeaders()
        });
      }catch(e){
        console.log(e)
      }
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

  async findUser(loginValue,passwordValue) {
    try{
      return new Promise(resolve => {
        setTimeout(() => {
          resolve('El mundo del reves');
        }, 2000);
      })
    }catch(error){
      console.log(error)
    }
    /*return await fetch(this.BASE_URL, {
          method:'POST',
          headers: this.createHeaders(),
          body: JSON.stringify(item),
        });
      }*/
  }
}
  export default Api;
  